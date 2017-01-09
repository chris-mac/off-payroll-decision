/*
 * Copyright 2017 HM Revenue & Customs
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package uk.gov.hmrc.offpayroll.service

import uk.gov.hmrc.offpayroll.PropertyFileLoader
import uk.gov.hmrc.offpayroll.models.{OUT, UNKNOWN}
import uk.gov.hmrc.play.test.{UnitSpec, WithFakeApplication}

import scala.concurrent.ExecutionContext.Implicits.global


/**
  * Created by peter on 09/12/2016.
  */
class FlowServiceSpec extends UnitSpec with WithFakeApplication {

  private val personalService = PropertyFileLoader.transformMapFromQuestionTextToAnswers("personalService")
  private val csrf = "csrf"
  private val fullPlusJunk:Map[String,String] = personalService + (csrf -> "112361283681230")

  //@TODO work out how to stub the decision service when not running in Integration test mode
  val flowservice: FlowService = IR35FlowService

  "A Flow Service " should {
    " be able to get the start of an Interview" in {
      flowservice.getStart() should not be (null)
    }
  }

  val interview: Map[String, String] = Map("personalService.contractualObligationForSubstitute" -> "Yes")
  val currentElement: (String, String) = "personalService.contractualObligationForSubstitute" -> "Yes"

  it should {
    "Process a partial personalService and expect it to return Continue" in {

      val intervieEvalResult = await(flowservice.evaluateInterview(interview, currentElement))

      assert(intervieEvalResult.continueWithQuestions === true, "Only a partial personalService so we need to continue")

      val nextValidTag = "personalService.contractualObligationInPractice"

      assert(intervieEvalResult.element.head.questionTag === nextValidTag) //next tag
    }
  }

  it should {
    " be able to get the current currentElement" in {
      assert(flowservice.getAbsoluteElement(0, 1).questionTag == "personalService.contractualObligationInPractice")
    }
  }




}