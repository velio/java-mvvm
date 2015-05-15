/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.velyo.mvvm;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 *
 * @author velyo.ivanov
 */
public interface Model {
    ModelState getModelState();
    Class<?> getModelType();
    void process(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException;
}
