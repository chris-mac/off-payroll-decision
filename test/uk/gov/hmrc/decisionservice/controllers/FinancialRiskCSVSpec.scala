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

package uk.gov.hmrc.decisionservice.controllers

import uk.gov.hmrc.decisionservice.Versions
import uk.gov.hmrc.play.test.{UnitSpec, WithFakeApplication}

/**
  * Created by work on 09/01/2017.
  */
class FinancialRiskCSVSpec extends UnitSpec with WithFakeApplication with DecisionControllerFinalCsvSpec {
  val FINANCIAL_RISK_SCENARIO_0 = s"/test-scenarios/${Versions.VERSION2}/financial-risk/scenario_0.csv"

  "POST /decide" should {
    s"return 200 and expected decision for financial risk scenario 0 for version ${Versions.VERSION2}" in {
      createRequestSendVerifyDecision(FINANCIAL_RISK_SCENARIO_0, Versions.VERSION2)
    }
  }
}