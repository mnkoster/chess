package handler;

import dataaccess.DataAccessException;
import io.javalin.http.Context;
import service.ClearService;

public class ClearHandler {

    private final ClearService clearService;

    public ClearHandler(ClearService clearService) {
        this.clearService = clearService;
    }

    public void handle(Context ctx) {
        try {
            clearService.clear();
            ctx.status(200).json(new Object());
        } catch (DataAccessException e) {
            ctx.status(500).json(new ErrorResponse("Error: server error"));
        }
    }
}
