package org.zerock.service;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.zerock.domain.FileVO;
import org.zerock.mapper.FileupMapper;

import com.oracle.bmc.ConfigFileReader;
import com.oracle.bmc.Region;
import com.oracle.bmc.auth.ConfigFileAuthenticationDetailsProvider;
import com.oracle.bmc.objectstorage.ObjectStorage;
import com.oracle.bmc.objectstorage.ObjectStorageClient;
import com.oracle.bmc.objectstorage.requests.DeleteObjectRequest;
import com.oracle.bmc.objectstorage.requests.GetObjectRequest;
import com.oracle.bmc.objectstorage.requests.PutObjectRequest;
import com.oracle.bmc.objectstorage.responses.DeleteObjectResponse;
import com.oracle.bmc.objectstorage.responses.GetObjectResponse;
import com.oracle.bmc.objectstorage.transfer.UploadConfiguration;
import com.oracle.bmc.objectstorage.transfer.UploadManager;
import com.oracle.bmc.objectstorage.transfer.UploadManager.UploadRequest;
import com.oracle.bmc.objectstorage.transfer.UploadManager.UploadResponse;

import lombok.Setter;
import lombok.extern.log4j.Log4j;

@Service
@Log4j
public class FileUpService {
	
	@Setter(onMethod_ = @Autowired)
	private String ociConfigPath;
	
	@Setter(onMethod_ = @Autowired)
	private FileupMapper mapper;
	
	public void transfer(MultipartFile file, String fileName) throws Exception {
		String profile = "DEFAULT";

		String objectName = file.getOriginalFilename();
		
		if (fileName != null) {
			objectName = fileName;
		}
		
		String contentType = file.getContentType();
		InputStream is = file.getInputStream();
		long size = file.getSize();

		Map<String, String> metadata = null;
		String contentEncoding = null;
		String contentLanguage = null;

		final ConfigFileReader.ConfigFile configFile = ConfigFileReader.parse(ociConfigPath);

		final ConfigFileAuthenticationDetailsProvider provider = new ConfigFileAuthenticationDetailsProvider(
				configFile);

		String namespaceName = configFile.get("namespace_name");
		String bucketName = configFile.get("bucket_name");

		ObjectStorage client = new ObjectStorageClient(provider);
		client.setRegion(Region.AP_SEOUL_1);

		// configure upload settings as desired
		UploadConfiguration uploadConfiguration = UploadConfiguration.builder().allowMultipartUploads(true)
				.allowParallelUploads(true).build();

		UploadManager uploadManager = new UploadManager(client, uploadConfiguration);

		PutObjectRequest request = PutObjectRequest.builder().bucketName(bucketName).namespaceName(namespaceName)
				.objectName(objectName).contentType(contentType).contentLanguage(contentLanguage)
				.contentEncoding(contentEncoding).opcMeta(metadata).build();

		UploadRequest uploadDetails = UploadRequest.builder(is, size).allowOverwrite(true).build(request);

		UploadResponse response = uploadManager.upload(uploadDetails);

		// fetch the object just uploaded
		GetObjectResponse getResponse = client.getObject(GetObjectRequest.builder().namespaceName(namespaceName)
				.bucketName(bucketName).objectName(objectName).build());
	}
	
	public List<FileVO> readFiles(Long bno) {
		log.info("readFiles..........");
		return mapper.readFiles(bno);
	} 
	
	public boolean deleteWithBoard(Long bno) {
		log.info("deleteWithBoard.......");
		int cnt = mapper.deleteWithBoard(bno);
		return cnt == 1;
	}
	
	public void register(FileVO file) {
		log.info("register........" + file);
		mapper.insert(file);
	}
	
	public boolean modify(FileVO file) {
		log.info("modify..............");
		int cnt = mapper.update(file);
		return cnt == 1;
	}
	
	
	public void fileDelete(String fileName) throws Exception {
		String profile = "DEFAULT";

		String objectName = "";

		if (fileName != null) {
			objectName = fileName;
		}else {
			throw new Exception();
		}

		final ConfigFileReader.ConfigFile configFile = ConfigFileReader.parse(ociConfigPath);

		final ConfigFileAuthenticationDetailsProvider provider = new ConfigFileAuthenticationDetailsProvider(
				configFile);

		String namespaceName = configFile.get("namespace_name");
		String bucketName = configFile.get("bucket_name");

		ObjectStorage client = new ObjectStorageClient(provider);
		client.setRegion(Region.AP_SEOUL_1);
		
		//create a request and dependent object
		DeleteObjectRequest request = DeleteObjectRequest.builder()
				.bucketName(bucketName).namespaceName(namespaceName)
				.objectName(objectName).build();
		
		//send request to the Client
		DeleteObjectResponse deleteResponse = client.deleteObject(request);
	}
	 
	
	public void write(MultipartFile file) {
		write(file, null);
	}
	
	public void write(MultipartFile file, String filename) {
		String path = "/Temp/" + filename;
		
		if(filename == null) {
			path = "/temp" + file.getOriginalFilename();
		}

		try (
			InputStream is = file.getInputStream();
			BufferedInputStream bis = new BufferedInputStream(is);

			FileOutputStream os = new FileOutputStream(path);
			BufferedOutputStream bos = new BufferedOutputStream(os);
			) {
			
			byte[] buffer = new byte[1024];
			int b = 0;
			while ((b = is.read(buffer)) != -1) {
				bos.write(buffer, 0, b);
			}
		} catch (Exception e) {
			log.error(e.getMessage());
			e.printStackTrace();
		}
	}

}
