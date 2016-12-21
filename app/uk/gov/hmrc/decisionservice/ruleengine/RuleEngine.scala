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

package uk.gov.hmrc.decisionservice.ruleengine

import cats.data.{Validated, Xor}
import play.api.Logger
import uk.gov.hmrc.decisionservice.Validation
import uk.gov.hmrc.decisionservice.model.DecisionServiceError
import uk.gov.hmrc.decisionservice.model.rules._

import scala.annotation.tailrec

sealed trait RuleEngineDecision {
  def value: String
  def facts: Map[String,CarryOver]
  def isFinal: Boolean
}

case class RuleEngineDecisionUndecided(facts: Map[String,CarryOver]) extends RuleEngineDecision {
  override def value = "Unknown"
  override def isFinal = false
}

case class RuleEngineDecisionImpl(value: String, facts: Map[String,CarryOver]) extends RuleEngineDecision {
  override def isFinal = true
}

object FinalFact {
  def unapply(facts: Facts) = facts.facts.values.find(_.exit)
}

trait RuleEngine {
  def processRules(rules: Rules, facts: Facts): Validation[RuleEngineDecision] = {
    @tailrec
    def go(rules: List[SectionRuleSet], facts: Facts): Validation[Facts] = {
      facts match {
        case FinalFact(_) => Validated.valid(facts)
        case _ =>
          rules match {
            case Nil => Validated.valid(facts)
            case ruleSet :: ruleSets =>
              ruleSet ==+>: facts match {
                case Validated.Valid(newFacts) => go(ruleSets, newFacts)
                case e@Validated.Invalid(_) => e
              }
          }
      }
    }
    val maybeFacts = go(rules.rules, facts)
    maybeFacts.map {
      case f@FinalFact(ff) =>
        Logger.debug(s"decision found: '${ff.value}'\n")
        RuleEngineDecisionImpl(ff.value, f.facts)
      case f =>
        Logger.debug(s"decision not found - undecided\n")
        RuleEngineDecisionUndecided(f.facts)
    }
  }
}

object RuleEngineInstance extends RuleEngine