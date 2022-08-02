build: 
	cargo build --all

release: 
	cargo build --all --release

clean:
	cargo clean

check:
	cargo check --all

test:
	cargo test --all

fmt:
	cargo fmt --all

lint:
	cargo clippy --all --all-targets