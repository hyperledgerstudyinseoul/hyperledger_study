package com.blockchain.board.controller;

import java.io.File;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.bouncycastle.asn1.ocsp.Request;
import org.hyperledger.fabric.sdk.Channel;
import org.hyperledger.fabric.sdk.HFClient;
import org.hyperledger.fabric.sdk.NetworkConfig;
import org.hyperledger.fabric.sdk.ProposalResponse;
import org.hyperledger.fabric.sdk.User;
import org.hyperledger.fabric.sdk.security.CryptoSuite;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.blockchain.board.common.vo.BoardUser;
import com.blockchain.board.service.BoardService;





@Controller
public class BoardController {

	private static final Logger logger = LoggerFactory.getLogger(BoardController.class);
	
	@Resource(name="boardService")
	private BoardService boardService;
	

	    private static final long waitTime = 6000;
	    private static String connectionProfilePath;
	    private static final String TEST_FIXTURES_PATH = "chaincode";
	    private static String channelName = "mychannel";
	    private static String userName = "admin";
	    private static String secret = "adminpw";
	    private static String chaincodeName = "samplecc";
	    private static String chaincodeVersion = "1.0";
	
	
	@RequestMapping(value = "/board/board_list", method = RequestMethod.GET)
	public String board_list(HttpServletRequest req, HttpServletResponse res, Model model) {
		logger.info("/board/board_write GET");	
		
		
		String ccName=req.getParameter("ccName");
        String funcName=req.getParameter("funcName");
        String arg=req.getParameter("arg");
        String channelName=req.getParameter("channelName");
        channelName="mychannel";
        
		 connectionProfilePath = System.getProperty("user.dir") + "/network-config.yaml";
	        File f = new File(connectionProfilePath);
	        
	        
	        try {

	            NetworkConfig networkConfig = NetworkConfig.fromYamlFile(f);
	            NetworkConfig.OrgInfo clientOrg = networkConfig.getClientOrganization();
	            NetworkConfig.CAInfo caInfo = clientOrg.getCertificateAuthorities().get(0);
	            
	            
	            
	            BoardUser user = boardService.getBoardUser(clientOrg, caInfo,networkConfig);

	            HFClient client = HFClient.createNewInstance();
	            client.setCryptoSuite(CryptoSuite.Factory.getCryptoSuite());
	            client.setUserContext(user);

	            Channel channel = client.loadChannelFromConfig(channelName, networkConfig);

	            //service discovery function.
	            //Peer p = channel.getPeers().iterator().next();
	            //channel.removePeer(p);
	            //channel.addPeer(p, Channel.PeerOptions.createPeerOptions().addPeerRole(Peer.PeerRole.SERVICE_DISCOVERY));
	            //Collection<String> cc = channel.getDiscoveredChaincodeNames();

	            channel.initialize();

	            channel.registerBlockListener(blockEvent -> {
	                logger.info(String.format("Receive block event (number %s) from %s", blockEvent.getBlockNumber(), blockEvent.getPeer()));
	            });

	            ProposalResponse response = null;
	            
	            
			/*
			 * String[] args = arg.split(","); for (int i=0; i<args.length; i++) { args[i] =
			 * args[i].trim(); }
			 */
	            ccName="samplecc";
	            funcName="funcName";
	            String args[] = null;
	            args[0]="park";
	            
	            
	            response=boardService.executeChaincode(client, channel, ccName, funcName, args, false);

	            
	            //deployChaincode(client, channel, "example_cc", "/github.com/example_cc", "1.0.0");
	            logger.info("Shutdown channel.");
	            
	            channel.shutdown(true);

	        } catch (Exception e) {
	            logger.error("exception", e);
	        }
		
		
		boardService.boardInsert();
		
		
		return "home";
	}
	
	@RequestMapping(value = "/board/board_info", method = RequestMethod.GET)
	public String board_info(HttpServletRequest req, HttpServletResponse res, Model model) {
		logger.info("/board/board_write GET");
		
		return "home";
	}
	
	@RequestMapping(value = "/board/board_write", method = RequestMethod.GET)
	public String board_write(HttpServletRequest req, HttpServletResponse res, Model model) {
		logger.info("/board/board_write GET");
		
		return "home";
	}
	
	
	
	
	
	
	
}
