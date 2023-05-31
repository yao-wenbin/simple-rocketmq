package com.yaowb.rocketmq.acl;

import com.yaowb.rocketmq.common.concurrent.ServiceThread;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Author yaowenbin
 * @Date 2023/5/31
 */
@Slf4j
public class AclFileWatchService extends ServiceThread {

    private final String aclDir;

    private final String defaultAclFile;

    private int lastAclFileCounts;

    private Map<String, Long > fileLastmodifyMap;

    private List<String/*file path*/> filenameList;

    private final Listener listenr;

    private static final int WATCH_INTERVAL = 5000;

    public AclFileWatchService(String dir, String defaultAclFile, final AclFileWatchService.Listener listener) {
        this.aclDir = dir;
        this.defaultAclFile = defaultAclFile;
        this.listenr = listener;

        this.filenameList = new ArrayList<>();

        this.filenameList = AclFileReader.loadAclFiles(aclDir, defaultAclFile);

        this.lastAclFileCounts = this.filenameList.size();

        this.fileLastmodifyMap = computeLastModifileMap();
    }

    private HashMap<String, Long> computeLastModifileMap() {
        HashMap<String, Long> newLastModifyMap = new HashMap<>(this.filenameList.size());

        this.filenameList.forEach(filename -> {
            newLastModifyMap.put(filename, new File(filename).lastModified());
        });

        return newLastModifyMap;
    }

    @Override
    public String getServiceName() {
        return "AclFileWatchService";
    }

    @Override
    public void run() {
        log.info(getServiceName() + "started");

        while (!isStopped()) {
            waitForRunning(WATCH_INTERVAL);

            filenameList.clear();

            this.filenameList = AclFileReader.loadAclFiles(aclDir, defaultAclFile);

            int currentAclFileCounts = filenameList.size();

            if (lastAclFileCounts != currentAclFileCounts) {
                log.info("aclFileNum change, last: {}, current: {}", lastAclFileCounts, currentAclFileCounts);
                handleAclFileCountChange(currentAclFileCounts);
            } else {
                filenameList.forEach(filename -> {
                    long aclFilelastModified = new File(filename).lastModified();
                    if (!fileLastmodifyMap.get(filename).equals(aclFilelastModified)) {
                        log.info("aclFile change, filename: {}", filename);

                        fileLastmodifyMap.put(filename, aclFilelastModified);
                        listenr.onFileChange(filename);
                    }
                });
            }


        }
    }

    private void handleAclFileCountChange(int currentAclFileCounts) {
        listenr.onFileCountChange(aclDir);
        lastAclFileCounts = currentAclFileCounts;
        fileLastmodifyMap = computeLastModifileMap();
    }


    /**
     * AclFile Change Listener
     */
    public interface Listener {
        /**
         * will be called when the target file is changed.
         * @param aclFileName
         * the changed file absolute path.
         */
        void onFileChange(String aclFileName);

        /**
         * will be called when the count of acl files is changed.
         * @param path the path of the acl dir.
         */
        void onFileCountChange(String path);

    }
}
