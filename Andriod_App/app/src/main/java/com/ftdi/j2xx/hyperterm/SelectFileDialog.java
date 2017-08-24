package com.ftdi.j2xx.hyperterm;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.List;

import com.ftdi.j2xx.hyperterm.ListenerList.FireHandler;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Handler;
import android.os.Message;

// The dialog to select File/Directory
public class SelectFileDialog extends Dialog {
	static final int MSG_SELECT_FOLDER_NOT_FILE = 7;
	
	Handler j2xxhyper_handler;
	private int iActionCode;
    private static final String PARENT_DIR = "..";
    private final String TAG = "file";    
    private String[] fileList;
    private File currentPath;
    private File chosenFolder;

    public interface FileSelectedListener {
        void fileSelected(File file);
    }
    public interface DirectorySelectedListener {
        void directorySelected(File directory);
    }
    private ListenerList<FileSelectedListener> fileListenerList = new ListenerList<SelectFileDialog.FileSelectedListener>();
    private ListenerList<DirectorySelectedListener> dirListenerList = new ListenerList<SelectFileDialog.DirectorySelectedListener>();
    private final Activity activity;
    private boolean selectDirectoryOption;
    private String fileEndsWith;    

    public SelectFileDialog(Activity activity, Handler handler, File path) {
    	super(activity);
        this.activity = activity;
        j2xxhyper_handler = handler;
        if (!path.exists()) path = android.os.Environment.getExternalStorageDirectory();
        loadFileList(path);
    }

    public Dialog createFileDialog() {
        Dialog dialog = null;
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);

        builder.setTitle(currentPath.getPath());
        if (selectDirectoryOption) {
            builder.setPositiveButton("Select Directory", new OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    DLog.e(TAG,"onClick:"+ currentPath.getPath());
                    fireDirectorySelectedEvent(currentPath);
                    
                    chosenFolder = currentPath;
                    Message msg = j2xxhyper_handler.obtainMessage(iActionCode);
    				j2xxhyper_handler.sendMessage(msg);

                }
            });
        }

        builder.setItems(fileList, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                String fileChosen = fileList[which];
                File chosenFile = getChosenFile(fileChosen);

                if (chosenFile.isDirectory()) {
                    loadFileList(chosenFile);
                    dialog.cancel();
                    dialog.dismiss();
                    showDialog();
                } 
                else
                {
                	if (selectDirectoryOption)
                	{
                        Message msg = j2xxhyper_handler.obtainMessage(MSG_SELECT_FOLDER_NOT_FILE);
        				j2xxhyper_handler.sendMessage(msg);
                	}
                	else
                	{
                		fireFileSelectedEvent(chosenFile);
                	}
	            }
            }
        });

        dialog = builder.show();
        return dialog;
    }


    public void addFileListener(FileSelectedListener listener) {
        fileListenerList.add(listener);
    }

    public void removeFileListener(FileSelectedListener listener) {
        fileListenerList.remove(listener);
    }

    public void setSelectDirectoryOption(boolean selectDirectoryOption) {
        this.selectDirectoryOption = selectDirectoryOption;
    }

    public void addDirectoryListener(DirectorySelectedListener listener) {
        dirListenerList.add(listener);
    }

    public void removeDirectoryListener(DirectorySelectedListener listener) {
        dirListenerList.remove(listener);
    }

    public void showDialog() {
        createFileDialog().show();
    }

    private void fireFileSelectedEvent(final File file) {
        fileListenerList.fireEvent(new FireHandler<SelectFileDialog.FileSelectedListener>() {
            public void fireEvent(FileSelectedListener listener) {
            	DLog.e(TAG,"fileListenerList fireEvent...");
                listener.fileSelected(file);
                
				Message msg = j2xxhyper_handler.obtainMessage(iActionCode);
				j2xxhyper_handler.sendMessage(msg);
            }
        });
    }

    private void fireDirectorySelectedEvent(final File directory) {
        dirListenerList.fireEvent(new FireHandler<SelectFileDialog.DirectorySelectedListener>() {
        	
        	
            public void fireEvent(DirectorySelectedListener listener) {
            	DLog.e(TAG,"fireEvent...");
            	
                listener.directorySelected(directory);
                
                DLog.e(TAG,"iActionCode:" + iActionCode);
                DLog.e(TAG,"directory:" + directory.toString());
                Message msg = j2xxhyper_handler.obtainMessage(iActionCode);
				j2xxhyper_handler.sendMessage(msg);
            }
        });
    }

    private void loadFileList(File path) {
        this.currentPath = path;
        List<String> r = new ArrayList<String>();
        if (path.exists()) {
            if (path.getParentFile() != null) r.add(PARENT_DIR);
            FilenameFilter filter = new FilenameFilter() {
                public boolean accept(File dir, String filename) {
                    File sel = new File(dir, filename);
                    if (!sel.canRead()) return false;
                    if (selectDirectoryOption) return sel.isDirectory();
                    else {
                        boolean endsWith = fileEndsWith != null ? filename.toLowerCase().endsWith(fileEndsWith) : true;
                        return endsWith || sel.isDirectory();
                    }
                }
            };
            String[] fileList1 = path.list(filter);
            for (String file : fileList1) {
                r.add(file);
            }
        }
        fileList = (String[]) r.toArray(new String[]{});
    }

    public File getChosenFile(String fileChosen) {
        if (fileChosen.equals(PARENT_DIR)) return currentPath.getParentFile();
        else return new File(currentPath, fileChosen);
    }

    public void setFileEndsWith(String fileEndsWith) {
        this.fileEndsWith = fileEndsWith != null ? fileEndsWith.toLowerCase() : fileEndsWith;
    }
    
    public void setActionCode(int actionCode)
    {
    	this.iActionCode = actionCode;    	
    }
    
    public File getChosenFolder() {
        return chosenFolder;
    }
 }

class ListenerList<L> 
{
	private List<L> listenerList = new ArrayList<L>();
	
	public interface FireHandler<L> {
	    void fireEvent(L listener);
	}
	
	public void add(L listener) {
	    listenerList.add(listener);
	}
	
	public void fireEvent(FireHandler<L> fireHandler) {
	    List<L> copy = new ArrayList<L>(listenerList);
	    for (L l : copy) {
	        fireHandler.fireEvent(l);
	    }
	}
	
	public void remove(L listener) {
	    listenerList.remove(listener);
	}
	
	public List<L> getListenerList() {
	    return listenerList;
	}
}
