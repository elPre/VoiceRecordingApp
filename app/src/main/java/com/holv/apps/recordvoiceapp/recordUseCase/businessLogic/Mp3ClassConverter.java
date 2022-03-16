package com.holv.apps.recordvoiceapp.recordUseCase.businessLogic;

import static com.arthenica.mobileffmpeg.Config.RETURN_CODE_CANCEL;
import static com.arthenica.mobileffmpeg.Config.RETURN_CODE_SUCCESS;

import android.app.Application;
import android.os.Environment;
import android.util.Log;

import com.arthenica.mobileffmpeg.Config;
import com.arthenica.mobileffmpeg.FFmpeg;
import com.arthenica.mobileffmpeg.FFprobe;
import com.arthenica.mobileffmpeg.MediaInformation;

import java.io.File;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

public class Mp3ClassConverter implements Mp3Converter {

    @Override
    public void convertToMp3(String fileName, String path, Application app) {
        File pathToDoc = app.getExternalFilesDir(Environment.DIRECTORY_MUSIC);

        String filePath = pathToDoc.getPath() + "/audiorecord.m4a";
        String outPutFile = pathToDoc.getPath() + "/"+fileName+".mp3";

        StringBuilder sb = new StringBuilder("-i ")
                .append(filePath)
                .append(" -c:v copy -c:a libmp3lame -q:a 0 ")//need to pass the number through parameter
                .append(outPutFile);

        int rc = FFmpeg.execute(sb.toString());

        if (rc == RETURN_CODE_SUCCESS) {
            Log.d("Mp3ClassConverter", "File converted to MP3");
            //delete old  file
        } else if (rc == RETURN_CODE_CANCEL) {
            Log.d("Mp3ClassConverter", "Command execution cancelled by user.");
            //alert the user the recording could not
        } else {
            Log.d("Mp3ClassConverter", String.format("Command execution failed with rc=%d and the output below.", rc));
            Config.printLastCommandOutput(Log.INFO);
        }
    }
}
