package com.example.jimmy.eventtec.retrofit;

import com.example.jimmy.eventtec.DTO.Departamento;
import com.example.jimmy.eventtec.DTO.Eventos;
import com.example.jimmy.eventtec.DTO.User;


import java.util.ArrayList;
import java.util.List;

import retrofit.Call;
import retrofit.http.GET;
import retrofit.http.PUT;
import retrofit.http.Path;

import com.example.jimmy.eventtec.DTO.User;
/**
 * Created by Jimmy on 12/05/2016.
 */
public interface ServerAPI {
    @GET("log/{id}/{pass}")
    Call<ArrayList<User>> log(@Path("id") String id,@Path("pass") String pass);

    @GET("getOwnMessage/{id}")
    Call<ArrayList<Eventos>> eventos(@Path("id") String id);

    @GET("delleteownmensaje/{idm}/{idp}")
    Call<String> borrarMensaje(@Path("idm") String idm,@Path("idp")String idp);

    @GET("getDepartementPerson/{id}")
    Call<ArrayList<Departamento>> getDepartementPerson(@Path("id") String id);

    @PUT("setVisto/{id}/{idm}")
    Call<String> setVisto(@Path("id") String id,@Path("idm") int idm);

/*
    @GET("/Clientes")
    void getClientesInformation(Callback<ArrayList<Clientes>> usersCallback);

    @GET("/Clientes/Cliente/{id}")
    void getClienteInformation(@Path("id") int id, Callback<Clientes> usersCallback);

    @POST("/Clientes/Cliente")
    void postClientesInformation(@Body Clientes post, Callback<Boolean> postCallback);

    @DELETE("/Clientes/Cliente/{id}")
    void delClientesInformation(@Path("id") int id, Callback<Boolean> postCallback);

    @PUT("/Clientes/Cliente/{id}")
    void putClientesInformation(@Path("id") int id, @Body Clientes clientes, Callback<Boolean> postCallback);

    //Para relaciones
    @GET("/Producto/Cliente/{id}")
    void getProductosdelcliente(@Path("id") int id,  Callback<ArrayList<Productos>> productosCallback);

    @GET("/Producto/{idp}/Cliente/{id}")
    void verificarClienteProducto(@Path("idp") int idp, @Path("id") int id, Callback<Boolean> usersCallback);
*/
}
