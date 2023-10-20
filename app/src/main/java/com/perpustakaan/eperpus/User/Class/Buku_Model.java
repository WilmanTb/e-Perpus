package com.perpustakaan.eperpus.User.Class;

import java.io.Serializable;

public class Buku_Model implements Serializable {
    String judulBuku;
    String stokBuku;
    String pengarang;
    String gambar;

    public Buku_Model(String judulBuku, String stokBuku, String pengarang, String bahasa, String tahunTerbit, String kategori, String jumlahHalaman) {
        this.judulBuku = judulBuku;
        this.stokBuku = stokBuku;
        this.pengarang = pengarang;
        this.bahasa = bahasa;
        this.tahunTerbit = tahunTerbit;
        this.kategori = kategori;
        this.jumlahHalaman = jumlahHalaman;
    }

    String bahasa;

    public Buku_Model() {

    }

    public Buku_Model(String judulBuku, String stokBuku, String pengarang, String gambar, String bahasa, String kodeBuku, String tahunTerbit, String namaPenerbit, String kategori, String jumlahHalaman) {
        this.judulBuku = judulBuku;
        this.stokBuku = stokBuku;
        this.pengarang = pengarang;
        this.gambar = gambar;
        this.bahasa = bahasa;
        this.kodeBuku = kodeBuku;
        this.tahunTerbit = tahunTerbit;
        this.namaPenerbit = namaPenerbit;
        this.kategori = kategori;
        this.jumlahHalaman = jumlahHalaman;
    }

    public String getBahasa() {
        return bahasa;
    }

    public void setBahasa(String bahasa) {
        this.bahasa = bahasa;
    }

    public String getKodeBuku() {
        return kodeBuku;
    }

    public void setKodeBuku(String kodeBuku) {
        this.kodeBuku = kodeBuku;
    }

    public String getTahunTerbit() {
        return tahunTerbit;
    }

    public void setTahunTerbit(String tahunTerbit) {
        this.tahunTerbit = tahunTerbit;
    }

    String kodeBuku;
    String tahunTerbit;

    public String getNamaPenerbit() {
        return namaPenerbit;
    }

    public void setNamaPenerbit(String namaPenerbit) {
        this.namaPenerbit = namaPenerbit;
    }

    public String getKategori() {
        return kategori;
    }

    public void setKategori(String kategori) {
        this.kategori = kategori;
    }

    public String getJumlahHalaman() {
        return jumlahHalaman;
    }

    public void setJumlahHalaman(String jumlahHalaman) {
        this.jumlahHalaman = jumlahHalaman;
    }

    String namaPenerbit;
    String kategori;
    String jumlahHalaman;


    public String getJudulBuku() {
        return judulBuku;
    }

    public void setJudulBuku(String judulBuku) {
        this.judulBuku = judulBuku;
    }

    public String getStokBuku() {
        return stokBuku;
    }

    public void setStokBuku(String stokBuku) {
        this.stokBuku = stokBuku;
    }

    public String getPengarang() {
        return pengarang;
    }

    public void setPengarang(String pengarang) {
        this.pengarang = pengarang;
    }

    public String getGambar() {
        return gambar;
    }

    public void setGambar(String gambar) {
        this.gambar = gambar;
    }

}
