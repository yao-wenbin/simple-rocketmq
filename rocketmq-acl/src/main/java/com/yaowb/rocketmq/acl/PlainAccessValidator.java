package com.yaowb.rocketmq.acl;

import com.yaowb.rocketmq.common.utils.FileUtils;
import com.yaowb.rocketmq.common.utils.MixAll;
import com.yaowb.rocketmq.remoting.RemotingCommand;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Author yaowenbin
 * @Date 2023/5/31
 * 为了简化类的数量，这里不使用桥接模式， 原本的实现中PlainAccessValidator是一个用于桥接AccessValidator和PlainAcessResource相关规则的类
 */
@Slf4j
public class PlainAccessValidator implements AccessValidator{

    private final String defaultAclDir = FileUtils.joinFilepath(MixAll.ROCKETMQ_HOME, "conf", "acl");

    private final String defaultAclFilename = FileUtils.joinFilepath(MixAll.ROCKETMQ_HOME, "conf", "plain_acl.yml");

    private Map<String/* fileFullPath*/,
                Map<String /* accessKey */, PlainAccessResource>> aclPlainAccessResourceMap = new HashMap<>(16);

    private Map<String/* AccessKey */, String /* fullPath */> accessKeyMap = new HashMap<>(16);

    private RemoteAddrStrategyFactory globalWhiteRemoteAddrStrategyFactory = new RemoteAddrStrategyFactory();

    private Map<String/* aclfilename */, List<RemoteAddrStrategy>> aclfileAddressStrategyMap= new HashMap<>(16);

    private boolean isWatchStart;

    private List<String> filenameList = new ArrayList<>();

    private final PermissionChecker permissionChecker = new PlainPermissionChecker();

    public PlainAccessValidator() {

        assureDefaultAclFileExists();

        filenameList = AclFileReader.loadAclFiles(defaultAclDir, defaultAclFilename);


        for (String aclFilename : filenameList) {
            PlainAccessData plainAccessData = FileUtils.loadYml(aclFilename, PlainAccessData.class);
            if (plainAccessData == null) {
                log.warn("acfile without data, filename: {}", aclFilename);
                continue;
            }
            log.info("plain acl conf data: {}", plainAccessData);

            // load globalWhiteRemoteAddressStrategy.
            List<RemoteAddrStrategy> globalWhiteRemoteAddressStrategyList = new ArrayList<>();
            List<String> globalWhiteRemoteAddresses = plainAccessData.getGlobalWhiteRemoteAddress();
            if (globalWhiteRemoteAddresses != null && !globalWhiteRemoteAddresses.isEmpty()) {
                for (String remoteAddr : globalWhiteRemoteAddresses) {
                    RemoteAddrStrategy strategy = globalWhiteRemoteAddrStrategyFactory.getStrategy(remoteAddr);
                    globalWhiteRemoteAddressStrategyList.add(strategy);
                }
            }

            if (globalWhiteRemoteAddressStrategyList.size() > 0) {
                aclfileAddressStrategyMap.put(aclFilename, globalWhiteRemoteAddressStrategyList);
            }

//            // load AccessResourceAcount.
//            List<PlainAccessAccount> accounts = plainAccessData.getAccounts();
//            if (CollUtil.isNotEmpty(accounts)) {
//                Map<String, PlainAccessResource> plainAccessResourceMap = new HashMap<>();
//
//                for (PlainAccessAccount account : accounts) {
//
//                    plainAccessResourceMap.put(account.getAccessKey(), );
//
//                }
//
//            }
        }
    }

    private void assureDefaultAclFileExists() {
//        TODO

    }


    @Override
    public AccessResource parse(RemotingCommand request, String remoteAddr) {
        return PlainAccessResource.parse(request, remoteAddr);
    }

    @Override
    public void validate(AccessResource accessResource) {

    }

}
