/*
 * Copyright 2016 HM Revenue & Customs
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

package uk.gov.hmrc.decisionservice.model.api

import play.api.libs.json.{Format, Json}
import uk.gov.hmrc.decisionservice.model.rules.CarryOver


case class DecisionRequest(version:String, correlationID:String, interview:Map[String,Map[String,String]])

object DecisionRequest {
  implicit val questionSetFormat: Format[DecisionRequest] = Json.format[DecisionRequest]
}

case class Score( score:Map[String,String])

object Score {
  implicit val scoreFormat: Format[Score] = Json.format[Score]
  val elements = List("control", "financial_risk_a", "financial_risk_b", "part_and_parcel", "business_structure", "personal_service")
  def create(facts:Map[String,CarryOver]):Map[String,String] =
    facts.toList.collect { case (a,co) if (Score.elements.contains(a)) => (a,co.value)}.toMap
  def createRaw(m:Map[String,String]) = m
}

case class DecisionResponse(version:String, correlationID:String, carryOnWithQuestions: Boolean, score:Map[String,String], result:String)

object DecisionResponse {
  implicit val decisionResponseFormat: Format[DecisionResponse] = Json.format[DecisionResponse]
}

case class ErrorResponse(code:Int, message:String)

object ErrorResponse {
  implicit val errorResponseFormat: Format[ErrorResponse] = Json.format[ErrorResponse]
}