package com.acme.hr.benefits.mock;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class MockApiServlet extends HttpServlet {

    private static final String PRODUCTS = "[" +
        "{\"itemId\":\"100\",\"name\":\"Medical Plan - Gold\",\"desc\":\"Comprehensive medical coverage\",\"price\":450.00,\"quantity\":999}," +
        "{\"itemId\":\"101\",\"name\":\"Medical Plan - Silver\",\"desc\":\"Standard medical coverage\",\"price\":320.00,\"quantity\":999}," +
        "{\"itemId\":\"102\",\"name\":\"Dental Plan - Standard\",\"desc\":\"Preventive and basic dental\",\"price\":85.00,\"quantity\":999}," +
        "{\"itemId\":\"103\",\"name\":\"Vision Plan - Standard\",\"desc\":\"Annual eye exams and lens coverage\",\"price\":45.00,\"quantity\":999}," +
        "{\"itemId\":\"104\",\"name\":\"Life Insurance - Basic\",\"desc\":\"Basic term life insurance\",\"price\":25.00,\"quantity\":999}" +
        "]";

    private static final String EMPTY_CART = "{\"shoppingCartItemList\":[],\"cartTotal\":0.0}";

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");
        String path = req.getPathInfo() != null ? req.getPathInfo() : req.getServletPath();
        if (path.contains("/products")) {
            resp.getWriter().write(PRODUCTS);
        } else {
            resp.getWriter().write(EMPTY_CART);
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        doGet(req, resp);
    }

    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        doGet(req, resp);
    }
}
