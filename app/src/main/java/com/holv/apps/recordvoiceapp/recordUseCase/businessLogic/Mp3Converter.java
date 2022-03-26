package com.holv.apps.recordvoiceapp.recordUseCase.businessLogic;

import java.io.File;

public interface Mp3Converter {

    File convertToMp3(InfoCovertToMp3 infoCovertToMp3);
}