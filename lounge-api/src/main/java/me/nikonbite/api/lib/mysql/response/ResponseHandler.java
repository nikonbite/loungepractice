package me.nikonbite.api.lib.mysql.response;

public interface ResponseHandler<H, R> {
  R handleResponse(H rs) throws Exception;
}
