CRYPTO_DEV_OPTIONS=--manifest-path=crypto/Cargo.toml

buid: build-crypto

release: release-crypto

clean: clean-crypto

check: check-crypto

test: test-crypto

fmt: fmt-crypto

lint: lint-crypto

build-crypto: 
	cargo build $(CRYPTO_DEV_OPTIONS)

release-crypto: 
	cargo build $(CRYPTO_DEV_OPTIONS)

clean-crypto:
	cargo clean $(CRYPTO_DEV_OPTIONS)

check-crypto:
	cargo check $(CRYPTO_DEV_OPTIONS)

test-crypto:
	cargo test $(CRYPTO_DEV_OPTIONS)

fmt-crypto:
	cargo fmt $(CRYPTO_DEV_OPTIONS)

lint-crypto:
	cargo clippy $(CRYPTO_DEV_OPTIONS)