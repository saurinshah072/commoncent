package com.commoncents.utils.generatepdf.pdf;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.pdf.PdfDocument;
import android.os.Build;
import android.os.Environment;
import android.os.StrictMode;
import android.text.TextUtils;
import android.view.View;

import androidx.annotation.IdRes;
import androidx.annotation.LayoutRes;
import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.core.content.ContextCompat;

import com.commoncents.R;
import com.commoncents.utils.generatepdf.model.FailureResponse;
import com.commoncents.utils.generatepdf.model.SuccessResponse;
import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.TedPermission;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class PdfGenerator {

    public static double postScriptThreshold = 0.75;
    public final static int a4HeightInPX = 3508;
    public final static int a4WidthInPX = 2480;
    public final static int a5HeightInPX = 1748;
    public final static int a5WidthInPX = 2480;

    public static int a4HeightInPostScript = (int) (a4HeightInPX * postScriptThreshold);
    public static int a4WidthInPostScript = (int) (a4WidthInPX * postScriptThreshold);

    public static int AS_LIKE_XML_WIDTH = 0, AS_LIKE_XML_HEIGHT = 0;

    public static ContextStep getBuilder() {
        return new Builder();
    }

    public enum PageSize {
        /**
         * For standard A4 size page
         *
         * @deprecated For printing well-formed ISO standard sized papers(like-A4,A5 sized pdf,you
         * don't need to be concerned about width and height.Please set width and height to the xml
         * with an aspect ratio 1:√2. For example if your xml width is 100 dp then the height of the
         * xml will be (100 X √2) = 142 dp. Finally when we print them with any kind of ISO standard
         * paper, then they will be auto scaled and fit into the specific paper.
         * Reference:http://tolerancing.net/engineering-drawing/paper-size.html
         */
        @Deprecated
        A4,

        /**
         * For standard A5 size page
         *
         * @deprecated For printing well-formed ISO standard sized papers(like-A4,A5 sized pdf,you
         * don't need to be concerned about width and height.Please set width and height to the xml
         * with an aspect ratio 1:√2. For example if your xml width is 100 dp then the height of the
         * xml will be (100 X √2) = 142 dp. Finally when we print them with any kind of ISO standard
         * paper, then they will be auto scaled and fit into the specific paper.
         * Reference:http://tolerancing.net/engineering-drawing/paper-size.html
         */
        @Deprecated
        A5,
        /**
         * For print the page as much as they are big.
         */
        AS_LIKE_XML
    }


    public interface ContextStep {
        FromSourceStep setContext(Context context);
    }

    public interface FromSourceStep {
        LayoutXMLSourceIntakeStep fromLayoutXMLSource();

        ViewIDSourceIntakeStep fromViewIDSource();

        ViewSourceIntakeStep fromViewSource();
    }


    public interface ViewSourceIntakeStep {
        /**
         * @param viewList MUST NEED TO set android:layout_width A FIXED
         *                 VALUE INSTEAD OF "wrap_content" and "match_parent" OTHERWISE SIZING COULD BE MALFORMED
         *                 IN PDF FOR DIFFERENT DEVICE SCREEN
         */
        FileNameStep fromView(View... viewList);

        /**
         * @param viewList MUST NEED TO set android:layout_width A FIXED
         *                 VALUE INSTEAD OF "wrap_content" and "match_parent" OTHERWISE SIZING COULD BE MALFORMED
         *                 IN PDF FOR DIFFERENT DEVICE SCREEN
         */
        FileNameStep fromViewList(List<View> viewList);
    }

    public interface LayoutXMLSourceIntakeStep {
        FileNameStep fromLayoutXML(@LayoutRes Integer... layoutXMLs);

        FileNameStep fromLayoutXMLList(@LayoutRes List<Integer> layoutXMLList);
    }

    public interface ViewIDSourceIntakeStep {
        /**
         * @param containingActivity Host activity where all views reside.
         * @param xmlResourceList    The view ids which will be printed.
         */
        FileNameStep fromViewID(@NonNull Activity containingActivity,
                                @IdRes Integer... xmlResourceList);

        FileNameStep fromViewIDList(@NonNull Activity containingActivity,
                                    @IdRes List<Integer> xmlResourceList);
    }

    public interface PageSizeStep {
        FileNameStep setPageSize(PageSize pageSize);
    }

    public interface FileNameStep {
        Build setFileName(String fileName);
    }

    public interface Build {
        void build(PdfGeneratorListener pdfGeneratorListener);

        Build setFolderNameOrPath(String folderName);

        Build actionAfterPDFGeneration(ActionAfterPDFGeneration open);

        Build setPrintingMode(PrintingMode printingMode);

    }

    public enum ActionAfterPDFGeneration {
        OPEN, SHARE
    }

    public enum PrintingMode {
        LANDSCAPE, PORTRAIT
    }


    public static class Builder implements Build, FileNameStep, PageSizeStep
            , LayoutXMLSourceIntakeStep, ViewSourceIntakeStep, ViewIDSourceIntakeStep
            , FromSourceStep, ContextStep {
        private static final int NO_XML_SELECTED_YET = -1;
        private int pageWidthInPixel = AS_LIKE_XML_WIDTH;
        private int pageHeightInPixel = AS_LIKE_XML_HEIGHT;
        private Context context;
        private PdfGeneratorListener pdfGeneratorListener;
        private List<View> viewList = new ArrayList<>();
        private String fileName;
        private String targetPdf;
        private ActionAfterPDFGeneration actionAfterPDFGeneration = ActionAfterPDFGeneration.OPEN;
        private String folderName;
        private String directoryPath;
        private Disposable disposable;
        private PrintingMode printingMode = PrintingMode.PORTRAIT;

        private void postFailure(String errorMessage) {
            FailureResponse failureResponse = new FailureResponse(errorMessage);
            postLog(errorMessage);
            if (pdfGeneratorListener != null)
                pdfGeneratorListener.onFailure(failureResponse);
        }

        private void postFailure(Throwable throwable) {
            FailureResponse failureResponse = new FailureResponse(throwable);
            if (pdfGeneratorListener != null)
                pdfGeneratorListener.onFailure(failureResponse);
        }

        private void postLog(String logMessage) {
            if (pdfGeneratorListener != null)
                pdfGeneratorListener.showLog(logMessage);
        }

        private void postOnGenerationStart() {
            if (pdfGeneratorListener != null)
                pdfGeneratorListener.onStartPDFGeneration();
        }

        private void postOnGenerationFinished() {
            if (pdfGeneratorListener != null)
                pdfGeneratorListener.onFinishPDFGeneration();
        }

        private void postSuccess(PdfDocument pdfDocument, File file, int widthInPS, int heightInPS) {
            if (pdfGeneratorListener != null)
                pdfGeneratorListener.onSuccess(new SuccessResponse(pdfDocument, file, widthInPS, heightInPS));
        }

        /**
         * We should reset the value of the page otherwise page size might be differ for each page
         */
        private void resetValue() {
            pageWidthInPixel = AS_LIKE_XML_WIDTH;
            pageHeightInPixel = AS_LIKE_XML_HEIGHT;
            postScriptThreshold = 0.75;
            a4HeightInPostScript = (int) (a4HeightInPX * postScriptThreshold);
        }

        private void print() {

            try {
                if (context != null) {
                    PdfDocument document = new PdfDocument();
                    if (viewList == null || viewList.size() == 0)
                        postLog("View list null or zero sized");
                    for (int i = 0; i < viewList.size(); i++) {
                        resetValue();
                        View content = viewList.get(i);
                        if (pageWidthInPixel == AS_LIKE_XML_WIDTH &&
                                pageHeightInPixel == AS_LIKE_XML_HEIGHT) {
                            pageHeightInPixel = content.getHeight();
                            pageWidthInPixel = content.getWidth();

                            if (pageHeightInPixel == 0 && pageWidthInPixel == 0) {
                                //If view was inflated from XML then getHeight() and getWidth()
                                //So we need to then make it measured.
                                if (content.getMeasuredWidth() == 0 && content.getMeasuredHeight() == 0) {
                                    /*content.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);*/
                                    content.measure(View.MeasureSpec.makeMeasureSpec(pageWidthInPixel, View.MeasureSpec.UNSPECIFIED),
                                            View.MeasureSpec.makeMeasureSpec(pageHeightInPixel, View.MeasureSpec.UNSPECIFIED));
                                }
                                pageHeightInPixel = content.getMeasuredHeight();
                                pageWidthInPixel = content.getMeasuredWidth();
                            }

                            postScriptThreshold = 1.0;
                            a4HeightInPostScript = pageHeightInPixel;
                        }


                        /*Convert page size from pixel into post script because PdfDocument takes
                         * post script as a size unit*/
                        pageHeightInPixel = (int) (pageHeightInPixel * postScriptThreshold);
                        pageWidthInPixel = (int) (pageWidthInPixel * postScriptThreshold);


                        content.measure(View.MeasureSpec.makeMeasureSpec(pageWidthInPixel, View.MeasureSpec.EXACTLY), View.MeasureSpec.UNSPECIFIED);
                        pageHeightInPixel = (Math.max(content.getMeasuredHeight(), a4HeightInPostScript));


                        PdfDocument.PageInfo pageInfo =
                                new PdfDocument.PageInfo.Builder((pageWidthInPixel), (pageHeightInPixel), i + 1).create();
                        PdfDocument.Page page = document.startPage(pageInfo);

                        content.layout(0, 0, pageWidthInPixel, pageHeightInPixel);
                        content.draw(page.getCanvas());

                        document.finishPage(page);

                        /*Finally invalidate it and request layout for restore the previous state
                         * of the view as like as the xml. Otherwise for generating PDF by view id,
                         * the main view is being messed up because this a view is not cloneable and
                         * being modified in the above view related tasks for printing PDF. */
                        content.invalidate();
                        content.requestLayout();

                    }

                    //This is for prevent crashing while opening generated PDF.
                    StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
                    StrictMode.setVmPolicy(builder.build());

                    setUpDirectoryPath(context);

                    if (TextUtils.isEmpty(directoryPath)) {
                        postFailure("Cannot find the storage path to create the pdf file.");
                        return;
                    }
                    if (folderName.contains("/storage/emulated/")) {
                        directoryPath = folderName + "/";
                    } else
                        directoryPath = directoryPath + "/" + folderName + "/";
                    File file = new File(directoryPath);
                    if (!file.exists()) {
                        if (!file.mkdirs()) {
                            postLog("Folder is not created." +
                                    "file.mkdirs() is returning false");
                        }
                        //Folder is made here
                    }

                    targetPdf = directoryPath + fileName + ".pdf";

                    File filePath = new File(targetPdf);
                    //File is created under the folder but not yet written.

                    disposeDisposable();
                    postOnGenerationStart();
                    disposable = Completable.fromAction(() -> document.writeTo(new FileOutputStream(filePath)))
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .doFinally(() -> {
                                document.close();
                                disposeDisposable();
                                postOnGenerationFinished();
                            })
                            .subscribe(() -> {
                                postSuccess(document, filePath, pageWidthInPixel, pageHeightInPixel);
                            }, this::postFailure);


                } else {
                    postFailure("Context is null");
                }
            } catch (
                    Exception e) {
                postFailure(e);
            }
        }


        private void disposeDisposable() {
            if (disposable != null && !disposable.isDisposed())
                disposable.dispose();
        }

        private void setUpDirectoryPath(Context context) {

            String state = Environment.getExternalStorageState();

            // Make sure it's available
            if (!TextUtils.isEmpty(state) && Environment.MEDIA_MOUNTED.equals(state)) {
                postLog("Your external storage is mounted");
                // We can read and write the media
                directoryPath = context.getExternalFilesDir(null) != null ?
                        context.getExternalFilesDir(null).getAbsolutePath() : "";

                if (TextUtils.isEmpty(directoryPath))
                    postLog("context.getExternalFilesDir().getAbsolutePath() is returning null.");

            } else {
                postLog("Your external storage is unmounted");
                // Load another directory, probably local memory
                directoryPath = context.getFilesDir() != null ? context.getFilesDir().getAbsolutePath() : "";
                if (TextUtils.isEmpty(directoryPath))
                    postFailure("context.getFilesDir().getAbsolutePath() is also returning null.");
                else postLog("PDF file creation path is " + directoryPath);
            }
        }

        private boolean hasAllPermission(Context context) {
            if (context == null) {
                postFailure("Context is null");
                return false;
            }
            return ContextCompat.checkSelfPermission(context,
                    Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED &&
                    ContextCompat.checkSelfPermission(context,
                            Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED;
        }

        @RequiresApi(api = android.os.Build.VERSION_CODES.Q)
        @Override
        public void build(PdfGeneratorListener pdfGeneratorListener) {
            this.pdfGeneratorListener = pdfGeneratorListener;
            if (hasAllPermission(context)) {
                print();
            } else {
                postLog("WRITE_EXTERNAL_STORAGE and READ_EXTERNAL_STORAGE Permission is not given." +
                        " Permission taking popup (using https://github.com/Karumi/Dexter) is going " +
                        "to be shown");
                //add permission code


                PermissionListener permissionlistener = new PermissionListener() {
                    @Override
                    public void onPermissionGranted() {
                        postLog("onPermissionGranted permission: " + "Granted permission:");
                    }

                    @Override
                    public void onPermissionDenied(List<String> deniedPermissions) {
                        for (int i = 0; i < deniedPermissions.size(); i++) {
                            postLog("Denied permission: " + deniedPermissions.get(i));
                        }
                    }
                };

                TedPermission.with(context)
                        .setPermissionListener(permissionlistener)
                        .setDeniedMessage(context.getString(R.string.permission_denied))
                        .setPermissions(
                                Manifest.permission.ACCESS_MEDIA_LOCATION,
                                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                                Manifest.permission.READ_EXTERNAL_STORAGE)
                        .check();
            }
        }


        @Override
        public FileNameStep fromView(View... viewArrays) {
            this.viewList = Utils.getViewListMeasuringRoot(
                    new ArrayList<>(Arrays.asList(viewArrays)),
                    pdfGeneratorListener);
            return this;
        }

        @Override
        public FileNameStep fromViewList(List<View> viewList) {
            this.viewList = Utils.getViewListMeasuringRoot(viewList, pdfGeneratorListener);
            return this;
        }


        @Override
        public Build actionAfterPDFGeneration(ActionAfterPDFGeneration actionAfterPDFGeneration) {
            this.actionAfterPDFGeneration = actionAfterPDFGeneration;
            return this;
        }

        @Override
        public Build setPrintingMode(PrintingMode printingMode) {
            this.printingMode = printingMode;
            return this;
        }


        @Override
        public FromSourceStep setContext(Context context) {
            this.context = context;
            return this;
        }

        @Override
        public FileNameStep setPageSize(PageSize pageSize) {
            return this;
        }

        @Override
        public Build setFolderNameOrPath(String folderName) {
            this.folderName = folderName;
            return this;
        }

        @Override
        public Build setFileName(String fileName) {
            this.fileName = fileName;
            return this;
        }


        @Override
        public FileNameStep fromViewID(@NonNull Activity activity, @IdRes Integer... xmlResourceList) {
            this.viewList = Utils.getViewListFromID(activity, Arrays.asList(xmlResourceList), pdfGeneratorListener);
            return this;
        }

        @Override
        public FileNameStep fromViewIDList(@NonNull Activity activity, List<Integer> viewIDList) {
            this.viewList = Utils.getViewListFromID(activity, viewIDList, pdfGeneratorListener);
            return this;
        }


        @Override
        public FileNameStep fromLayoutXML(@LayoutRes Integer... layouts) {
            this.viewList = Utils.getViewListFromLayout(context, pdfGeneratorListener, Arrays.asList(layouts));
            return this;
        }

        @Override
        public FileNameStep fromLayoutXMLList(@LayoutRes List<Integer> layoutXMLList) {
            this.viewList = Utils.getViewListFromLayout(context, pdfGeneratorListener, layoutXMLList);
            return this;
        }

        @Override
        public LayoutXMLSourceIntakeStep fromLayoutXMLSource() {
            return this;
        }

        @Override
        public ViewIDSourceIntakeStep fromViewIDSource() {
            return this;
        }

        @Override
        public ViewSourceIntakeStep fromViewSource() {
            return this;
        }
    }


}
