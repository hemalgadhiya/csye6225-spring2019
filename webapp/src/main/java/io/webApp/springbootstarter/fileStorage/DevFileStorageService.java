package io.webApp.springbootstarter.fileStorage;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Date;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.SdkClientException;
import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.auth.InstanceProfileCredentialsProvider;
import com.amazonaws.auth.profile.ProfileCredentialsProvider;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.DeleteObjectRequest;
import com.amazonaws.services.s3.model.PutObjectRequest;

@Service
@Profile("dev")
public class DevFileStorageService implements FileStorageService {

	private AmazonS3 s3client;
	
	private Path fileStorageLocation;
	
	private final static Logger logger = LoggerFactory.getLogger(DevFileStorageService.class);


	@Value("${endpointUrl}")
	private String endpointUrl;
	@Value("${bucketName}")
	private String bucketName;
	/*
	 * @Value("${accessKey}") private String accessKey;
	 * 
	 * @Value("${secretKey}") private String secretKey;
	 */

	@PostConstruct
	private void initializeAmazon() {
//		AWSCredentials credentials = new BasicAWSCredentials(this.accessKey, this.secretKey);
//		this.s3client = new AmazonS3Client(credentials);
		
		//this.s3client = AmazonS3ClientBuilder.standard().withCredentials(new ProfileCredentialsProvider()).build();
		this.s3client = AmazonS3ClientBuilder.standard().withCredentials(new InstanceProfileCredentialsProvider(false)).build();
	}

	private File convertMultiPartToFile(MultipartFile file) throws IOException {
		File convFile = new File(file.getOriginalFilename());
		FileOutputStream fos = new FileOutputStream(convFile);
		fos.write(file.getBytes());
		fos.close();
		return convFile;

	}

	private String generateFileName(MultipartFile multiPart) {
		return multiPart.getOriginalFilename();
	}

	private void uploadFileTos3bucket(String fileName, File file) {
		try {
		s3client.putObject(new PutObjectRequest(bucketName, fileName, file));
		}catch (Exception e) {
			logger.error(e.toString());
		}
//				new PutObjectRequest(bucketName, fileName, file).withCannedAcl(CannedAccessControlList.PublicRead));
	}

	public String storeFile(MultipartFile multipartFile) {
		String fileUrl = "";
		try {
			File file = convertMultiPartToFile(multipartFile);
			String fileName = generateFileName(multipartFile);
			fileStorageLocation = Paths.get(endpointUrl + bucketName);
			fileUrl = fileStorageLocation + fileName;
			uploadFileTos3bucket(fileName, file);
			file.delete();
		} catch (Exception e) {
			logger.error(e.toString());
		}
		return fileUrl;
	}

	public boolean DeleteFile(String fileUrl) {
		 try {
		        String fileName = fileUrl.substring(fileUrl.lastIndexOf("/") + 1);
		        logger.debug("fileName : " + fileName);
		        s3client.deleteObject(new DeleteObjectRequest(bucketName, fileName));
		        }
		        catch (AmazonServiceException e) {
		            // The call was transmitted successfully, but Amazon S3 couldn't process
		            // it, so it returned an error response.
					logger.error(e.toString());
		            return false;
		            } catch (SdkClientException e) {
		            // Amazon S3 couldn't be contacted for a response, or the client
		            // couldn't parse the response from Amazon S3.
		    		logger.error(e.toString());
		            return false;
		            }
		        return true;
	}

	public Path getFileStorageLocation() {
		return fileStorageLocation;
	}

}