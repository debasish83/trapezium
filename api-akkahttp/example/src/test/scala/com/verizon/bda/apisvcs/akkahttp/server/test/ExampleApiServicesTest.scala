package com.verizon.bda.apisvcs.akkahttp.server.test

import java.util

import com.verizon.bda.apisvcs.{ApiHttpServices, SampleRouteHttpServices}
import com.verizon.bda.apisvcs.akkahttp.server.ApiAkkaHttpServer
import com.verizon.bda.apisvcs.utils.HttpServicesUtils._
import com.verizon.logger.BDALoggerFactory
import org.junit.runner.RunWith
import org.scalatest.{BeforeAndAfterAll, FunSuite}
import org.scalatest.junit.JUnitRunner
import com.verizon.bda.apisvcs.utils.SampleHttpSvcsConstans._
import com.verizon.bda.apisvcs.test.utils.ExampleSvcsTestUtils._
import com.verizon.bda.apisvcs.utils.HttpServicesConstants.{WSO2_AUTHORIZATION_DATA_KEY => _, _}
import org.apache.commons.httpclient.HttpClient
import org.apache.commons.httpclient.methods.{PostMethod, StringRequestEntity}

import scala.concurrent.ExecutionContext.Implicits.global


/**
  * Created by chundch on 4/27/17.
  */


@RunWith(classOf[JUnitRunner])
class ExampleApiServicesTest extends FunSuite with BeforeAndAfterAll {

  private val logger = BDALoggerFactory.getLogger(this.getClass)

  var apiServer: ApiAkkaHttpServer = null
  val JWTTOKEN_DATA_FILE_PATH = "src/test/data/"
  val JWTTOKEN_DATA_FILE = "wso2_assertiontoken.txt"
  var JWT_ASSERTION_TOKEN : String = null
  val hostname: String = "10.20.210.68" // getHostName
  var svcsToPublish : util.HashMap[String, ApiHttpServices] =
  new util.HashMap[String, ApiHttpServices]()

  /**
    * Helper to initialize required
    * object for the test
    */

  override def beforeAll() {
    super.beforeAll()
    svcsToPublish.put(SAMPLE_HTTP_SERVICE_PUBLISH_ENDPOINT, new SampleRouteHttpServices)
    apiServer = new ApiAkkaHttpServer
    apiServer.init(svcsToPublish)
    JWT_ASSERTION_TOKEN = getJwtAssertionToken(JWTTOKEN_DATA_FILE_PATH, JWTTOKEN_DATA_FILE)
    apiServer.start(getHostName, SAMPLE_HTTP_SERVICE_BINDING_PORT)
  }

  /**
    * Test example http service for tes endpoint
    */

  test("validate http services for test endpoint : " + SAMPLE_HTTP_SERVICE_PUBLISH_ENDPOINT) {

    logger.info("Testing example http service endpoint :" + SAMPLE_HTTP_SERVICE_PUBLISH_ENDPOINT)
    val noofroutes = 2
    val postendpoint = "http://" + hostname + ":" +
      SAMPLE_HTTP_SERVICE_BINDING_PORT + "/" + SAMPLE_HTTP_SERVICE_PUBLISH_ENDPOINT + "/" +
      SAMPLE_HTTP_SERVICE_RESOURCE
    val client = new HttpClient
    val postmethod = new PostMethod(postendpoint)
    postmethod.setRequestHeader(WSO2_AUTHORIZATION_DATA_KEY,
      JWT_ASSERTION_TOKEN)
    val postdatastr = "test request test example service sample resource "
    val reqentity = new StringRequestEntity(postdatastr, "plain/text" , "utf-8")
    postmethod.setRequestEntity(reqentity)
    val rescode = client.executeMethod(postmethod)
    assert(200 == rescode)
    val resdata = postmethod.getResponseBodyAsString
    assert(resdata.contains(postdatastr))

  }

  override def afterAll(): Unit = {

    super.afterAll()
    apiServer.stop()

  }




}