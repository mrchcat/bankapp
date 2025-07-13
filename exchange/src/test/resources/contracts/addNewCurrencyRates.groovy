package contracts

import org.springframework.cloud.contract.spec.Contract

Contract.make {
    description "отправка всех доступных курсов валют"
    request {
        method 'POST'
        url '/exchange'
        headers {
            contentType(applicationJson())
        }
        body(
             "baseCurrency": "RUB",
             "exchangeRates":
             [
               [
                 "currency": "USD",
                 "buyRate": 151,
                 "sellRate": 161,
                 "time": "2025-07-08T17:37:26.666306300"
               ],
               [
                 "currency": "CNY",
                 "buyRate": 15,
                 "sellRate": 16,
                 "time": "2025-07-08T17:37:26.666306300"
               ]
             ]
        )
    }
    response {
        status 204
    }
}