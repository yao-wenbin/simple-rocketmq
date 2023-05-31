package com.yaowb.rocketmq.acl;

import com.yaowb.rocketmq.common.utils.FileUtils;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * @Author yaowenbin
 * @Date 2023/5/31
 */

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class AclFileReader {

    public static List<String> loadAclFiles(String aclDir, String defaultAclFile) {
        List<String> aclFiles = loadAclFiles(aclDir);
        if (new File(defaultAclFile).exists() && !aclFiles.contains(defaultAclFile)) {
            aclFiles.add(defaultAclFile);
        }
        return aclFiles;
    }


    public static List<String> loadAclFiles(String aclDir) {
        List<String> aclFiles = new ArrayList<>();

        File dir = new File(aclDir);
        if (!dir.exists()) {
            throw new AclException("acl dir is not exists");
        }

        for (String aclFilename : dir.list()) {
            if (FileUtils.isYmlFile(aclFilename)) {
                aclFiles.add(aclFilename);
            }

            if (new File(aclFilename).isDirectory()) {
                aclFiles.addAll(loadAclFiles(aclDir));
            }

        }

        return aclFiles;
    }


}
