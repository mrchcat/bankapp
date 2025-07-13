package contracts

import org.springframework.cloud.contract.spec.Contract

Contract.make {
    description "отправка курса обмена двух конкретных валют"
    request {
        method 'GET'
        url '/exchange/RUB?toCurrency=RUB'
    }
    response {
        status 200
        headers {
            contentType(applicationJson())
        }
        body(
                "from": "RUB",
                "to": "RUB",
                "rate": 1
        )
    }
}
