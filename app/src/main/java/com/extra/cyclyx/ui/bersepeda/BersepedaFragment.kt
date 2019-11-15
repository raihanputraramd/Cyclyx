package com.extra.cyclyx.ui.bersepeda


import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.extra.cyclyx.R
import com.extra.cyclyx.database.AppDatabase
import com.extra.cyclyx.databinding.FragmentBersepedaBinding
import com.extra.cyclyx.utils.*
import com.extra.cyclyx.utils.service.TrackingService
import com.mapbox.geojson.Feature
import com.mapbox.geojson.FeatureCollection
import com.mapbox.geojson.LineString
import com.mapbox.geojson.Point
import com.mapbox.mapboxsdk.Mapbox
import com.mapbox.mapboxsdk.location.LocationComponentActivationOptions
import com.mapbox.mapboxsdk.location.modes.CameraMode
import com.mapbox.mapboxsdk.location.modes.RenderMode
import com.mapbox.mapboxsdk.maps.MapboxMap
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback
import com.mapbox.mapboxsdk.maps.Style
import com.mapbox.mapboxsdk.style.layers.CircleLayer
import com.mapbox.mapboxsdk.style.layers.LineLayer
import com.mapbox.mapboxsdk.style.layers.Property.LINE_JOIN_ROUND
import com.mapbox.mapboxsdk.style.layers.PropertyFactory.*
import com.mapbox.mapboxsdk.style.sources.GeoJsonSource

/**
 * A simple [Fragment] subclass.
 */
class BersepedaFragment : Fragment(), OnMapReadyCallback {
    private lateinit var binding: FragmentBersepedaBinding
    private lateinit var map: MapboxMap
    private lateinit var viewModel: BersepedaViewModel
    private lateinit var ctx: Context
    //map component
    private val SOURCE_ID = "SOURCE_ID"
    private val CIRCLE_LAYER_ID = "CIRCLE_LAYER_ID"
    private val LINE_LAYER_ID = "LINE_LAYER_ID"
    private val CIRCLE_COLOR = Color.RED
    private val LINE_COLOR = CIRCLE_COLOR
    private val CIRCLE_RADIUS: Float = 3f
    private val LINE_WIDTH: Float = 2f

    override fun onMapReady(mapboxMap: MapboxMap) {
        map = mapboxMap
        map.setStyle(
            Style.MAPBOX_STREETS
        ) {
            it.addSource(GeoJsonSource(SOURCE_ID))
            it.addLayer(
                CircleLayer(CIRCLE_LAYER_ID, SOURCE_ID).withProperties(
                    circleColor(CIRCLE_COLOR),
                    circleRadius(CIRCLE_RADIUS)
                )
            )
            it.addLayerBelow(
                LineLayer(LINE_LAYER_ID, SOURCE_ID).withProperties(
                    lineColor(LINE_COLOR),
                    lineWidth(LINE_WIDTH),
                    lineJoin(LINE_JOIN_ROUND)
                ), CIRCLE_LAYER_ID
            )
            initializeLocationComponent(it)
        }
    }

    private fun initializeLocationComponent(loadedMapStyle: Style) {
        val locationComponent = map.locationComponent
        locationComponent.activateLocationComponent(
            LocationComponentActivationOptions.builder(
                context!!, loadedMapStyle
            ).build()
        )
        locationComponent.isLocationComponentEnabled = true
        locationComponent.cameraMode = CameraMode.TRACKING
        locationComponent.renderMode = RenderMode.NORMAL
    }

    private fun addPointToMap(points: List<Point>) {
        map.getStyle {
            val geoJsonSource = it.getSourceAs<GeoJsonSource>(SOURCE_ID)
            if (geoJsonSource != null) {
                val lineString = LineString.fromLngLats(points)
                geoJsonSource.setGeoJson(
                    FeatureCollection.fromFeature(
                        Feature.fromGeometry(
                            lineString
                        )
                    )
                )
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        //init maps and binding
        Mapbox.getInstance(context!!, com.extra.cyclyx.BuildConfig.apikey)
        binding = FragmentBersepedaBinding.inflate(inflater)

        binding.lifecycleOwner = this
        //init context for use within fragment
        ctx = requireNotNull(this.context).applicationContext
        //init viewModel
        val application = requireNotNull(this.activity).application
        val dataSource = AppDatabase.getInstance(application).bersepedaDAO
        val viewModelFactory = BersepedaViewModelFactory(dataSource, application)
        viewModel =
            ViewModelProviders.of(this, viewModelFactory).get(BersepedaViewModel::class.java)

        //bind viewmodel
        binding.viewModel = viewModel
        binding.mapView.onCreate(savedInstanceState)
        binding.mapView.getMapAsync(this)

        //observe changes
        viewModel.locationPoints.observe(this, Observer {
            it?.let {
                addPointToMap(it)
            }
        })

        //init broadcast receiver
        LocalBroadcastManager.getInstance(ctx).registerReceiver(
            locationUpdateReceiver,
            IntentFilter("LocationUpdates")
        )

        modifyTrackingService(START_SERVICE)
        viewModel.startTimer()

        viewModel.trackingStatus.observe(this, Observer { status ->
            when (status) {
                TRACKING_STARTED, TRACKING_RESUMED -> {
                    binding.imgPause.setBackgroundResource(R.drawable.pause)
                    binding.imgPause.setOnClickListener {
                        modifyTrackingService(PAUSE_SERVICE)
                        viewModel.pauseTimer()
                    }

                    binding.imgStop.setOnClickListener {
                        modifyTrackingService(STOP_SERVICE)
                        viewModel.stopTimer()
                        activity?.finish()
                    }
                }
                TRACKING_PAUSED -> {
                    binding.imgPause.setBackgroundResource(R.drawable.play)
                    binding.imgPause.setOnClickListener {
                        modifyTrackingService(START_SERVICE)
                        viewModel.resumeTimer()
                    }

                    binding.imgStop.setOnClickListener {
                        viewModel.stopTimer()
                        activity?.finish()
                    }
                }
                TRACKING_STOPPED -> {
                    activity?.finish()
                }
            }
        })
        // Inflate the layout for this fragment
        return binding.root
    }

    private fun modifyTrackingService(act: String) {
        val intent = Intent(context, TrackingService::class.java)
        intent.action = act
        context?.startService(intent)
    }

    //receive service location update
    private val locationUpdateReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            if (intent != null) {
                val encodedString = intent.getStringExtra(ENCODED_STRING)
                viewModel.decodePolyLine(encodedString!!)
            }
        }
    }

    //necessary for mapbox mapview to adapt lifecycle
    @SuppressWarnings("MissingPermission")
    override fun onStart() {
        super.onStart()
        binding.mapView.onStart()
    }

    override fun onResume() {
        super.onResume()
        binding.mapView.onResume()
    }

    override fun onPause() {
        super.onPause()
        binding.mapView.onPause()
    }

    override fun onStop() {
        super.onStop()
        binding.mapView.onStop()
    }

    override fun onDestroy() {
        super.onDestroy()
        modifyTrackingService(STOP_SERVICE) //dont forget to stop service
        LocalBroadcastManager.getInstance(ctx)
            .unregisterReceiver(locationUpdateReceiver) // and stop receiving broadcast
        binding.mapView.onDestroy()
    }

    override fun onLowMemory() {
        super.onLowMemory()
        binding.mapView.onLowMemory()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        binding.mapView.onSaveInstanceState(outState)
    }
}