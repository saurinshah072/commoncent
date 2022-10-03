package com.commoncents.utils.generatepdf.pdf;

import com.commoncents.utils.generatepdf.model.FailureResponse;
import com.commoncents.utils.generatepdf.model.SuccessResponse;



public abstract class PdfGeneratorListener implements PdfGeneratorContract {
    @Override
    public void showLog(String log) {

    }

    @Override
    public void onSuccess(SuccessResponse response) {

    }

    @Override
    public void onFailure(FailureResponse failureResponse) {

    }
}
