package com.blockchain.board.service.impl;

import java.net.MalformedURLException;
import java.util.Collection;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hyperledger.fabric.sdk.ChaincodeID;
import org.hyperledger.fabric.sdk.Channel;
import org.hyperledger.fabric.sdk.Enrollment;
import org.hyperledger.fabric.sdk.HFClient;
import org.hyperledger.fabric.sdk.NetworkConfig;
import org.hyperledger.fabric.sdk.NetworkConfig.CAInfo;
import org.hyperledger.fabric.sdk.NetworkConfig.OrgInfo;
import org.hyperledger.fabric.sdk.NetworkConfig.UserInfo;
import org.hyperledger.fabric.sdk.ProposalResponse;
import org.hyperledger.fabric.sdk.TransactionProposalRequest;
import org.hyperledger.fabric.sdk.exception.ProposalException;
import org.hyperledger.fabric_ca.sdk.HFCAClient;
import org.hyperledger.fabric_ca.sdk.HFCAInfo;
import org.hyperledger.fabric_ca.sdk.exception.EnrollmentException;
import org.hyperledger.fabric_ca.sdk.exception.InfoException;
import org.hyperledger.fabric_ca.sdk.exception.InvalidArgumentException;
import org.springframework.stereotype.Service;

import com.blockchain.board.common.vo.BoardUser;
import com.blockchain.board.service.BoardService;

@Service
public class BoardServiceImpl implements BoardService  {

	private static final Log logger = LogFactory.getLog(BoardServiceImpl.class);
	private long proposalWaitTime = 5000;
	public void boardInsert() {
		// TODO Auto-generated method stub
		
	}

	
	public BoardUser getBoardUser(OrgInfo clientOrg, CAInfo caInfo, NetworkConfig networkConfig) throws EnrollmentException, InvalidArgumentException, MalformedURLException, InfoException {
		HFCAClient hfcaClient = HFCAClient.createNewInstance(caInfo);
        HFCAInfo cainfo = hfcaClient.info();
        UserInfo userinfo=caInfo.getRegistrars().iterator().next();
        
        String userName=userinfo.getName();
        String secret=userinfo.getEnrollSecret();
        
        logger.info("CA name: " + cainfo.getCAName());
        logger.info("CA version: " + cainfo.getVersion());

        // Persistence is not part of SDK.

        logger.info("Going to enroll user: " + userName);
        
        Enrollment enrollment = hfcaClient.enroll(userName, secret);
        logger.info("Enroll user: " + userName +  " successfully.");

        BoardUser user = new BoardUser();
        user.setMspId(clientOrg.getMspId());
        user.setName(userName);
        user.setOrganization(clientOrg.getName());
        user.setEnrollment(enrollment);
		return user;
	}


	
	public ProposalResponse executeChaincode(HFClient client, Channel channel,String ccName, String funcName, String[] args,boolean invoke) throws ProposalException, org.hyperledger.fabric.sdk.exception.InvalidArgumentException {
		Collection<ProposalResponse> txResponse= null;
		if(invoke=true) {
			TransactionProposalRequest txpr = client.newTransactionProposalRequest();
			txpr.setChaincodeID(ChaincodeID.newBuilder().setName(ccName).build());
//			txpr.setChaincodeLanguage(Type.GO_LANG);
			txpr.setFcn(funcName);
			txpr.setProposalWaitTime(proposalWaitTime);
			txpr.setArgs(args);

			 txResponse = channel.sendTransactionProposal(txpr);
			channel.sendTransaction(txResponse);
			return txResponse.iterator().next();
		}else {
			TransactionProposalRequest txpr = client.newTransactionProposalRequest();
			txpr.setChaincodeID(ChaincodeID.newBuilder().setName(ccName).build());
//			txpr.setChaincodeLanguage(Type.GO_LANG);
			txpr.setFcn(funcName);
			txpr.setProposalWaitTime(this.proposalWaitTime);
			txpr.setArgs(args);

			
			txResponse = channel.sendTransactionProposal(txpr);

		}
		
		return txResponse.iterator().next();
	}

}
