package org.sunbird.common.util;

import static org.junit.Assert.assertTrue;
import static org.powermock.api.mockito.PowerMockito.mock;
import static org.powermock.api.mockito.PowerMockito.mockStatic;
import static org.powermock.api.mockito.PowerMockito.when;
import static org.sunbird.common.models.util.JsonKey.*;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.sunbird.cloud.storage.BaseStorageService;
import org.sunbird.cloud.storage.factory.StorageServiceFactory;
import org.sunbird.common.exception.ProjectCommonException;
import org.sunbird.common.util.CloudStorageUtil.CloudStorageType;
import scala.Option;

@RunWith(PowerMockRunner.class)
@PowerMockIgnore({  "javax.management.*", "javax.net.ssl.*", "javax.security.*", "com.microsoft.azure.storage.*",
        "jdk.internal.reflect.*", "sun.security.ssl.*", "javax.crypto.*", "com.sun.org.apache.xerces.*", "javax.xml.*", "org.xml.*"})
@PrepareForTest({StorageServiceFactory.class, CloudStorageUtil.class})
public class CloudStorageUtilTest {

  String SIGNED_URL = "singedUrl";
  String UPLOAD_URL = "uploadUrl";

  @Before
  public void initTest() {
    BaseStorageService service = mock(BaseStorageService.class);
    mockStatic(StorageServiceFactory.class);

    try {
      when(StorageServiceFactory.class, "getStorageService", Mockito.any()).thenReturn(service);

      when(service.upload(
              Mockito.anyString(),
              Mockito.anyString(),
              Mockito.anyString(),
              Mockito.any(Option.class),
              Mockito.any(Option.class),
              Mockito.any(Option.class),
              Mockito.any(Option.class)))
          .thenReturn(UPLOAD_URL);

      when(service.getSignedURL(
              Mockito.anyString(),
              Mockito.anyString(),
              Mockito.any(Option.class),
              Mockito.any(Option.class)))
          .thenReturn(SIGNED_URL);

    } catch (Exception e) {
      Assert.fail(e.getMessage());
    }
  }

  @Test
  public void testGetAzureStorageTypeSuccess() {
    CloudStorageType storageType = CloudStorageType.getByName(AZURE_STR);
    assertTrue(CloudStorageType.AZURE.equals(storageType));
  }

  @Test
  public void testGetAwsStorageTypeSuccess() {
    CloudStorageType storageType = CloudStorageType.getByName(AWS_STR);
    assertTrue(CloudStorageType.AWS.equals(storageType));
  }

  @Test
  public void testGetGcpStorageTypeSuccess() {
    CloudStorageType storageType = CloudStorageType.getByName(GCLOUD_STR);
    assertTrue(CloudStorageType.GCLOUD.equals(storageType));
  }

  @Test(expected = ProjectCommonException.class)
  public void testGetStorageTypeFailureWithWrongType() {
    CloudStorageType.getByName("wrongstorage");
  }

  @Test
  @Ignore
  public void testUploadSuccess() {
    String result =
        CloudStorageUtil.upload(CloudStorageType.AZURE, "container", "key", "/file/path");
    assertTrue(UPLOAD_URL.equals(result));
  }

  @Test
  @Ignore
  public void testGetSignedUrlSuccess() {
    String signedUrl = CloudStorageUtil.getSignedUrl(CloudStorageType.AZURE, "container", "key");
    assertTrue(SIGNED_URL.equals(signedUrl));
  }
}
