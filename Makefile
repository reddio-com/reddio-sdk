CRYPTO_DEV_OPTIONS=--manifest-path=crypto/Cargo.toml
OUTPUT_DIR=./output
OUTPUT_HEADER_DIR=./output/include

build: build-crypto

release: release-crypto

clean: clean-crypto
	rm -rf output

check: check-crypto

test: test-crypto

fmt: fmt-crypto

lint: lint-crypto

build-crypto: generate-header
	cargo build $(CRYPTO_DEV_OPTIONS)

release-crypto: generate-header
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

cbindgen:
	cargo install cbindgen --root $(OUTPUT_DIR)
	chmod +x $(OUTPUT_DIR)/bin/cbindgen

generate-header: cbindgen
	$(OUTPUT_DIR)/bin/cbindgen --lang c crypto/src/lib.rs -o $(OUTPUT_HEADER_DIR)/crypto.h