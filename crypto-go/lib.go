package crypto

/*
#cgo linux LDFLAGS: -L../output/lib -lcrypto -Wl,-rpath=../output/lib
#cgo darwin LDFLAGS: -L../output/lib -lcrypto
#include "../output/include/crypto.h"
*/
import "C"
import (
	"errors"
	"math/big"
	"unsafe"
)

func Sign(privateKey, msgHash, seed *big.Int) (r, s *big.Int, err error) {
	if msgHash == nil || privateKey == nil {
		return nil, nil, errors.New("msgHash or privateKey is nil")
	}

	doc := C.SignDocument{
		msg_hash:    C.CString(msgHash.Text(16)),
		private_key: C.CString(privateKey.Text(16)),
	}

	if seed == nil {
		doc.seed = nil
	} else {
		doc.seed = C.CString(seed.Text(16))
	}

	sign := C.sign(doc)

	if sign.err != nil {
		defer C.free(unsafe.Pointer(sign.err))
		return nil, nil, errors.New(C.GoString(sign.err))
	}

	defer func() {
		C.free(unsafe.Pointer(sign.r))
		C.free(unsafe.Pointer(sign.s))
	}()

	r, s = new(big.Int), new(big.Int)
	_, ok := r.SetString(C.GoString(sign.r), 16)
	if !ok {
		return nil, nil, errors.New("r is not a valid big.Int")
	}
	_, ok = s.SetString(C.GoString(sign.s), 16)
	if !ok {
		return nil, nil, errors.New("s is not a valid big.Int")
	}
	return r, s, nil
}

func Verify(publicKey, msgHash, r, s *big.Int) (valid bool, err error) {
	if msgHash == nil || publicKey == nil || r == nil || s == nil {
		return false, errors.New("arguments cannot be nil")
	}

	signuture := C.Signature{
		msg_hash:   C.CString(msgHash.Text(16)),
		public_key: C.CString(publicKey.Text(16)),
		r:          C.CString(r.Text(16)),
		s:          C.CString(s.Text(16)),
	}

	verified := C.verify(signuture)

	if verified.err != nil {
		defer C.free(unsafe.Pointer(verified.err))
		return false, errors.New(C.GoString(verified.err))
	}

	return bool(verified.valid), nil
}

func GetPublicKey(privateKey *big.Int) (publicKey *big.Int, err error) {
	if privateKey == nil {
		return nil, errors.New("privateKey is nil")
	}

	public := C.get_public_key(C.CString(privateKey.Text(16)))

	if public.err != nil {
		defer C.free(unsafe.Pointer(public.err))
		return nil, errors.New(C.GoString(public.err))
	}

	defer func() {
		C.free(unsafe.Pointer(public.public_key))
	}()

	publicKey = new(big.Int)
	_, ok := publicKey.SetString(C.GoString(public.public_key), 16)
	if !ok {
		return nil, errors.New("public_key is not a valid big.Int")
	}
	return publicKey, nil
}
