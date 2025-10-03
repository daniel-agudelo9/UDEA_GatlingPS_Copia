package parabank

import io.gatling.core.Predef._
import io.gatling.http.Predef._
import scala.concurrent.duration._
import parabank.Data._

class transfer extends Simulation{

  // 1 Http Conf
  val httpConf = http.baseUrl(url)
    .acceptHeader("application/json")
    //Verificar de forma general para todas las solicitudes
    .check(status.is(200))

  // 2 Scenario Definition
  val scn = scenario("Escalabilidad en Transacciones").
    exec(http("Transferencia de fondos solicitados")
         .post("/transfer")
         .queryParam("fromAccountId", fromAccountId)
         .queryParam("toAccountId", toAccountId)
         .queryParam("amount", amount)
    )

  // 3 Load Scenario
  // 3 - Injection (closed model): ramp-up suave, luego steady-state a 100 concurrentes
  val injectionProfile = Seq(
    rampConcurrentUsers(0) to 100 during (30.seconds), // warm-up / ramp
    constantConcurrentUsers(100) during (3.minutes)    // steady state para medir p95
  )
