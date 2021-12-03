package com.cdp.blocnotas.entidades;

public class Notas {

    private int id;
    private String tipo;
    private String titulo;
    private String contenido;
    private String imagen;
    private String notavoz;
    private String rutaGaleria;

    public String getVideo() {
        return video;
    }

    public void setVideo(String video) {
        this.video = video;
    }

    private String video;

    public String getVideoGaleria() {
        return videoGaleria;
    }

    public void setVideoGaleria(String videoGaleria) {
        this.videoGaleria = videoGaleria;
    }

    private String videoGaleria;

    public String getRutaGaleria() {
        return rutaGaleria;
    }

    public void setRutaGaleria(String rutaGaleria) {
        this.rutaGaleria = rutaGaleria;
    }



    public String getNotavoz() {
        return notavoz;
    }

    public void setNotavoz(String notavoz) {
        this.notavoz = notavoz;
    }
    public String getImagen() {
        return imagen;
    }

    public void setImagen(String imagen) {
        this.imagen = imagen;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getContenido() {
        return contenido;
    }

    public void setContenido(String contenido) {
        this.contenido = contenido;
    }
}
