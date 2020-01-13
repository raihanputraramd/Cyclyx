package com.extra.cyclyx.ui.hasilBersepeda


import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.findNavController
import com.extra.cyclyx.R
import com.extra.cyclyx.database.AppDatabase
import com.extra.cyclyx.databinding.FragmentHasilBersepedaBinding
import com.extra.cyclyx.utils.*
import com.mapbox.geojson.Feature
import com.mapbox.geojson.FeatureCollection
import com.mapbox.geojson.LineString
import com.mapbox.geojson.Point
import com.mapbox.mapboxsdk.Mapbox
import com.mapbox.mapboxsdk.camera.CameraPosition
import com.mapbox.mapboxsdk.camera.CameraUpdateFactory
import com.mapbox.mapboxsdk.geometry.LatLng
import com.mapbox.mapboxsdk.maps.MapboxMap
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback
import com.mapbox.mapboxsdk.maps.Style
import com.mapbox.mapboxsdk.style.layers.CircleLayer
import com.mapbox.mapboxsdk.style.layers.PropertyFactory
import com.mapbox.mapboxsdk.style.layers.SymbolLayer
import com.mapbox.mapboxsdk.style.sources.GeoJsonSource
import com.mapbox.turf.TurfMeasurement

/**
 * A simple [Fragment] subclass.
 */
class HasilBersepedaFragment : Fragment(), OnMapReadyCallback {
    private lateinit var binding: FragmentHasilBersepedaBinding
    private lateinit var viewModel: HasilBersepedaViewModel
    private lateinit var map: MapboxMap

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        Mapbox.getInstance(context!!, resources.getString(R.string.mapbox_token))
        binding = FragmentHasilBersepedaBinding.inflate(inflater)
        binding.lifecycleOwner = this

        val application = requireNotNull(this.activity).application
        val arguments = HasilBersepedaFragmentArgs.fromBundle(arguments!!)
        val dataSource = AppDatabase.getInstance(application).bersepedaDAO

        binding.mapView.onCreate(savedInstanceState)
        binding.mapView.getMapAsync(this)

        viewModel =
            ViewModelProviders.of(this, HasilBersepedaViewModel.Factory(arguments.bersepedaKey, application)).get(HasilBersepedaViewModel::class.java)
        binding.viewModel = viewModel

        viewModel.routeList.observe(this, Observer { route ->
            route?.let {
                if (::map.isInitialized) {
                    addPointToMap(route)
                    val envelope = TurfMeasurement.envelope(
                        FeatureCollection.fromFeature(
                            Feature.fromGeometry(LineString.fromLngLats(route))
                        )
                    )
                    moveCamera(
                        TurfMeasurement.midpoint(route.first(), route.last()),
                        map,
                        determineZoomLevel(TurfMeasurement.length(envelope, "kilometers"))
                    )
                }
            }
        })

        viewModel.backToMenu.observe(this, Observer { status ->
            status?.let {
                if (it) {
                    this.findNavController()
                        .navigate(HasilBersepedaFragmentDirections.navigateToStatistikFromHasilBersepeda())
                    viewModel.doneNavigating()
                }
            }
        })
        // Inflate the layout for this fragment
        return binding.root
    }

    override fun onMapReady(mapboxMap: MapboxMap) {
        map = mapboxMap
        map.setMaxZoomPreference(18.0)
        map.setStyle(
            Style.MAPBOX_STREETS
        ) {
            it.addSource(GeoJsonSource(SOURCE_ID))
            it.addLayer(
                CircleLayer(CIRCLE_LAYER_ID, SOURCE_ID).withProperties(
                    PropertyFactory.circleColor(CIRCLE_COLOR),
                    PropertyFactory.circleRadius(CIRCLE_RADIUS)
                )
            )
            it.addLayer(
                SymbolLayer(SYMBOL_LAYER_ID, SOURCE_ID).withProperties(
                    PropertyFactory.iconImage(ICON_ID),
                    PropertyFactory.iconColor(Color.RED)
                )
            )
            viewModel.onMapAsyncFinished()
        }
    }

    private fun moveCamera(point: Point, map: MapboxMap, zoom: Double) {
        val cameraPosition = CameraPosition.Builder()
            .target(LatLng(point.latitude(), point.longitude()))
            .zoom(zoom)
            .build()

        map.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition))
    }

    private fun addPointToMap(points: List<Point>) {
        map.getStyle {
            val geoJsonSource = it.getSourceAs<GeoJsonSource>(SOURCE_ID)
            if (geoJsonSource != null) {
                val listFeature = ArrayList<Feature>()
                val lineString = LineString.fromLngLats(points)
                listFeature.add(
                    Feature.fromGeometry(
                        lineString
                    )
                )
                listFeature.add(
                    Feature.fromGeometry(
                        points.first()
                    )
                )
                listFeature.add(
                    Feature.fromGeometry(
                        points.last()
                    )
                )
                geoJsonSource.setGeoJson(
                    FeatureCollection.fromFeatures(
                        listFeature
                    )
                )
            }
        }
    }

    @SuppressWarnings("MissingPermission")
    override fun onStart() {
        Log.i("TRACKING", "ONSTART!")
        super.onStart()
        binding.mapView.onStart()
    }

    override fun onResume() {
        Log.i("TRACKING", "ONRESUME!")
        super.onResume()
        binding.mapView.onResume()
    }

    override fun onPause() {
        Log.i("TRACKING", "ONPAUSE!")
        super.onPause()
        binding.mapView.onPause()
    }

    override fun onStop() {
        Log.i("TRACKING", "ONSTOP!")
        super.onStop()
        binding.mapView.onStop()
    }

    override fun onDestroy() {
        Log.i("TRACKING", "ONDESTROY!")
        super.onDestroy()
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
