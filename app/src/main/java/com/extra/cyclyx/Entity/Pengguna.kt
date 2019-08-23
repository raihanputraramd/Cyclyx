package com.extra.cyclyx.Entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.*

@Entity(tableName = "Pengguna")
data class Pengguna(
    @PrimaryKey @ColumnInfo(name = "id_pengguna") var id: Int,
    @ColumnInfo(name = "username") var username: String,
    @ColumnInfo(name = "password") var password: String,
    @ColumnInfo(name = "nama_pengguna") var namaPengguna: String,
    @ColumnInfo(name = "jenis_kelamin") var jenisKelamin: String,
    @ColumnInfo(name = "tinggi_badan") var tinggiBadan: Int,
    @ColumnInfo(name = "berat_badan") var beratBadan: Int,
    @ColumnInfo(name = "usia") var usia: Int
)