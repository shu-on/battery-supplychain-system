package com.example.bcapp2.nfcread;

/**
 * Interface with callback functions for objects (most likely Activities) that
 * want to use the {@link Common#checkFileExistenceAndSave(java.io.File,
 * String[],boolean, android.content.Context, IActivityThatReactsToSave)}.
 * @author Gerhard Klostermeier
 */
public interface IActivityThatReactsToSave {

    /**
     * This method will be called after a successful save process.
     */
    void onSaveSuccessful();

    /**
     * This method will be called, if there was an error during the
     * save process or it the user hits "cancel" in the "file already exists"
     * dialog.
     */
    void onSaveFailure();
}