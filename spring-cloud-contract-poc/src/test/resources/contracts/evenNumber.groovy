import org.springframework.cloud.contract.spec.Contract

Contract.make {
    request {
        method 'POST'
        url '/v1/numbers/classification/2'
    }
    response {
        status 200
        body("""{"classification":"EVEN"}""")
        headers {
            contentType(applicationJson())
        }
    }
}