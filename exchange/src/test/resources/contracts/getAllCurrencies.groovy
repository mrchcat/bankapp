package contracts

import org.springframework.cloud.contract.spec.Contract

Contract.make {
    description "отправка всех доступных курсов валют"
    request {
        method 'GET'
        url '/exchange'
    }
    response {
        status 200
        headers {
            contentType(applicationJson())
        }
        body(
                [
                        [
                            "currency": "RUB",
                            "buyRate": 1,
                            "sellRate": 1,
                            "time": null
                        ]
                ]
        )
    }
}
