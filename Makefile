CRYPTO_DEV_OPTIONS=--manifest-path=crypto/Cargo.toml
OUTPUT_DIR=./output
OUTPUT_HEADER_DIR=./output/include
OUTPUT_LIB_DIR=./output/lib

export CGO_ENABLED=1

build: build-crypto build-crypto-go

release: release-crypto build-crypto-go

clean: clean-crypto
	rm -rf output

check: check-crypto build-crypto-go

test: test-crypto test-crypto-go

fmt: fmt-crypto fmt-crypto-go

lint: lint-crypto lint-crypto-go

build-crypto: generate-header
	mkdir -p $(OUTPUT_LIB_DIR)
	cargo build $(CRYPTO_DEV_OPTIONS)
	cp crypto/target/debug/libcrypto.* $(OUTPUT_LIB_DIR)

release-crypto: generate-header
	mkdir -p $(OUTPUT_LIB_DIR)
	cargo build $(CRYPTO_DEV_OPTIONS) --release
	cp crypto/target/release/libcrypto.* $(OUTPUT_LIB_DIR)

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
	mkdir -p $(OUTPUT_DIR)
	cargo install cbindgen --root $(OUTPUT_DIR)
	chmod +x $(OUTPUT_DIR)/bin/cbindgen

generate-header: cbindgen
	mkdir -p $(OUTPUT_HEADER_DIR)
	$(OUTPUT_DIR)/bin/cbindgen --lang c crypto/src/lib.rs -o $(OUTPUT_HEADER_DIR)/crypto.h

build-crypto-go: build-crypto
	cd crypto-go && go build

test-crypto-go: build-crypto
	cd crypto-go && go test

fmt-crypto-go:
	cd crypto-go && go fmt

lint-crypto-go:
	cd crypto-go && go vet
