package com.holv.apps.recordvoiceapp.recordUseCase.businessLogic;

import android.app.Application;

public interface Mp3Converter {

    void convertToMp3(String fileName, String path, Application app);

}
