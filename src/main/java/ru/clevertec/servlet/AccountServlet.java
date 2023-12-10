package ru.clevertec.servlet;

import com.google.gson.Gson;
import lombok.extern.slf4j.Slf4j;
import ru.clevertec.dto.AccountDto;
import ru.clevertec.service.AccountService;
import ru.clevertec.service.Service;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

import static javax.servlet.http.HttpServletResponse.SC_CREATED;
import static javax.servlet.http.HttpServletResponse.SC_OK;

@WebServlet(name = "account-servlet", value = "/account")
@Slf4j
public class AccountServlet extends HttpServlet {

    private final Service<AccountDto> accountService = AccountService.getInstance();
    private AccountDto accountDto;

    @Override
    public void init(ServletConfig config) throws ServletException {
        super.init();
        accountDto = AccountDto.builder().type("FF").build();
        log.info("init method from AccountServlet");
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

//        int number = 10 / 0;

        log.info("AccountDto from doGet method {}", accountDto);

        String signAppId = req.getParameter("signAppId");
        String type = req.getParameter("type");
        String currency = req.getParameter("currency");
        String status = req.getParameter("status");
        String paymentKindCode = req.getParameter("paymentKindCode");
        List<AccountDto> accounts = accountService.findByParams(signAppId, type, currency, status, paymentKindCode);
        String json = new Gson().toJson(accounts);
        try (PrintWriter out = resp.getWriter()) {
            out.write(json);
            resp.setStatus(SC_OK);
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        AccountDto accountDto = new Gson().fromJson(req.getReader(), AccountDto.class);
        String json = new Gson().toJson(accountService.save(accountDto));
        try (PrintWriter out = resp.getWriter()) {
            out.write(json);
            resp.setStatus(SC_CREATED);
        }
    }

    @Override
    public void destroy() {
        super.destroy();
        log.info("destroy method from AccountServlet");
    }
}
