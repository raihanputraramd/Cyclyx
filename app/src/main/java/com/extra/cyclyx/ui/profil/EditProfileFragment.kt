package com.extra.cyclyx.ui.profil


import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.core.view.isInvisible
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import com.extra.cyclyx.MainActivity
import com.extra.cyclyx.R
import com.extra.cyclyx.entity.User
import com.extra.cyclyx.utils.*
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlinx.android.synthetic.main.fragment_edit_profile.*

/**
 * A simple [Fragment] subclass.
 */
class EditProfileFragment : Fragment() {

    lateinit var firstName: EditText
    lateinit var lastName: EditText
    lateinit var birthYear: EditText
    lateinit var bodyWeight: EditText
    lateinit var bodyHeight: EditText
    lateinit var btnSave: FloatingActionButton

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_edit_profile, container, false)

        firstName = view.findViewById(R.id.edtNamaDepan)
        lastName = view.findViewById(R.id.edtNamaBelakang)
        birthYear = view.findViewById(R.id.edtTahunLahir)
        bodyWeight = view.findViewById(R.id.edtBerat)
        bodyHeight = view.findViewById(R.id.edtTinggi)
        btnSave = view.findViewById(R.id.btnSaveEdit)

        var sharedPreferences = activity!!.getSharedPreferences(SP_CYCLYX, Context.MODE_PRIVATE)

        val FirstName = sharedPreferences.getString(USER_FIRST_NAME, "")
        val LastName = sharedPreferences.getString(USER_LAST_NAME, "")
        val BirthYear = sharedPreferences.getInt(USER_BIRTHYEAR, 0)
        val UserWeight = sharedPreferences.getInt(USER_WEIGHT, 0)
        val UserHeight = sharedPreferences.getInt(USER_HEIGHT, 0)

        firstName.setText(FirstName)
        lastName.setText(LastName)
        birthYear.setText(BirthYear)
        bodyWeight.setText(UserWeight)
        bodyHeight.setText(UserHeight)


        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        btnSave.setOnClickListener {
            var sharedPreferences = activity!!.getSharedPreferences(SP_CYCLYX, Context.MODE_PRIVATE)

            var first_name: String = firstName.text.toString()
            var last_name: String = lastName.text.toString()
            var birth_year: String = birthYear.text.toString()
            var body_weight: String = bodyWeight.text.toString()
            var body_height: String = bodyHeight.text.toString()
            var editor: SharedPreferences.Editor = sharedPreferences.edit()

            when {
                first_name == "" -> profileValidate()
                last_name == "" -> profileValidate()
                birth_year == "" -> profileValidate()
                else -> {
                    editor.putString(USER_FIRST_NAME, first_name)
                    editor.putString(USER_LAST_NAME, last_name)
                    editor.putInt(USER_BIRTHYEAR, Integer.parseInt(birth_year))
                    editor.putInt(USER_WEIGHT, Integer.parseInt(body_weight))
                    editor.putInt(USER_HEIGHT, Integer.parseInt(body_height))

                    editor.apply()

                    val intent = Intent(context, MainActivity::class.java)
                    startActivity(intent)
                }
            }
        }
    }

    fun profileValidate() {
        namaDepanValidate()
        namaBelakangValidate()
        tahunValidate()
    }

    fun namaDepanValidate() {

        if (firstName.text.toString() == "") {
            tvValidateNamaDepan.isVisible
            tvNamaDepan.setTextColor(resources.getColor(R.color.redDanger))
            edtNamaDepan.setLinkTextColor(resources.getColor(R.color.redDanger))

        } else {
            tvValidateNamaDepan.isInvisible
            tvNamaDepan.setTextColor(resources.getColor(R.color.FontColorBlack))
            edtNamaDepan.setLinkTextColor(resources.getColor(R.color.FontColorBlack))
        }
    }

    fun namaBelakangValidate() {

        if (lastName.text.toString() == "") {
            tvValidateNamaBelakang.isVisible
            tvNamaBelakang.setTextColor(resources.getColor(R.color.redDanger))
            edtNamaBelakang.setLinkTextColor(resources.getColor(R.color.redDanger))
        } else {
            tvValidateNamaBelakang.isInvisible
            tvNamaBelakang.setTextColor(resources.getColor(R.color.FontColorBlack))
            edtNamaBelakang.setLinkTextColor(resources.getColor(R.color.FontColorBlack))
        }
    }

    fun tahunValidate() {
        if (birthYear.text.toString() == "") {
            tvValidateTahunLahir.isVisible
            tvTahunLahir.setTextColor(resources.getColor(R.color.redDanger))
            edtTahunLahir.setLinkTextColor(resources.getColorStateList(R.color.redDanger))
        } else {
            tvValidateTahunLahir.isInvisible
            tvTahunLahir.setTextColor(resources.getColor(R.color.FontColorBlack))
            edtTahunLahir.setLinkTextColor(resources.getColorStateList(R.color.FontColorBlack))
        }
    }

}
