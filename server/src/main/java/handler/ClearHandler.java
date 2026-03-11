package handler;

import dataaccess.DataAccessException;
import io.javalin.http.Context;
import service.ClearService;

/**
 * 2/28/26: added for p3 apis (date might be off)
 * 3/11/26: finalized for p4 implementation
 */
public class ClearHandler {

    private final ClearService clearService;

    public ClearHandler(ClearService clearService) {
        this.clearService = clearService;
    }

    public void handle(Context ctx) {
        try {
            clearService.clear();
            ctx.status(200);
        } catch (DataAccessException e) {
            ctx.status(500).json(new ErrorResponse("Error: server error"));
        }
    }
}
