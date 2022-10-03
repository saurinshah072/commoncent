package com.commoncents.utils.generatepdf.pdf;

import com.commoncents.utils.generatepdf.model.FailureResponse;
import com.commoncents.utils.generatepdf.model.SuccessResponse;

interface PdfGeneratorContract {
    void onSuccess(SuccessResponse response);

    void showLog(String log);

    void onStartPDFGeneration();

    void onFinishPDFGeneration();

    void onFailure(FailureResponse failureResponse);
}