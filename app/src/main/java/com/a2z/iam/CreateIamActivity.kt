package com.a2z.iam

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_create_iam.*


class CreateIamActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_iam)

        btn_create.setOnClickListener {
            createIam()
        }
    }

    fun createIam() {
        val kidoc = """
            {
                "id":"did:kidchain:abcdefg",
                "publicKey": [{
                    "id": "did:kidchain:123#_Qq0UL2Fq651Q0Fjd6TvnYE-faHiOpRlPVQcY_-tA4A",
                    "type": "JwsVerificationKey2020",
                    "controller": "did:example:123",
                    "publicKeyJwk": {
                      "crv": "Ed25519",
                      "x": "VCpo2LMLhn6iWku8MKvSLg2ZAoC-nlOyPVQaO3FxVeQ",
                      "kty": "OKP",
                      "kid": "_Qq0UL2Fq651Q0Fjd6TvnYE-faHiOpRlPVQcY_-tA4A"
                    }
                  }, {
                    "id": "did:example:123456789abcdefghi#keys-1",
                    "type": "Ed25519VerificationKey2018",
                    "controller": "did:example:pqrstuvwxyz0987654321",
                    "publicKeyBase58": "H3C2AVvLMv6gmMNam3uVAjZpfkcJCwDwnZn6z3wXmqPV"
                  }, {
                    "id": "did:example:123456789abcdefghi#keys-2",
                    "type": "Secp256k1VerificationKey2018",
                    "controller": "did:example:123456789abcdefghi",
                    "publicKeyHex": "02b97c30de767f084ce3080168ee293053ba33b235d7116a3263d29f1450936b71"
                  }],
                
                  "authentication": [
                    "did:example:123456789abcdefghi#keys-1",
                    {
                      "id": "did:example:123456789abcdefghi#keys-2",
                      "type": "Ed25519VerificationKey2018",
                      "controller": "did:example:123456789abcdefghi",
                      "publicKeyBase58": "H3C2AVvLMv6gmMNam3uVAjZpfkcJCwDwnZn6z3wXmqPV"
                    }
                  ],
        """.trimIndent()
        
        ev_name.text
    }
}