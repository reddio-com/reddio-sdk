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

	ret := C.SignResult{
		r: (*C.char)(C.malloc(C.size_t(C.BIG_INT_SIZE))),
		s: (*C.char)(C.malloc(C.size_t(C.BIG_INT_SIZE))),
	}

	errno := C.sign(doc, ret)

	if errno != C.Ok {
		return nil, nil, errors.New("unknow error")
	}

	r, s = new(big.Int), new(big.Int)
	_, ok := r.SetString(C.GoString(ret.r), 16)
	if !ok {
		return nil, nil, errors.New("r is not a valid big.Int")
	}
	_, ok = s.SetString(C.GoString(ret.s), 16)
	if !ok {
		return nil, nil, errors.New("s is not a valid big.Int")
	}
	return r, s, nil
}

func Verify(publicKey, msgHash, r, s *big.Int) (bool, error) {
	if msgHash == nil || publicKey == nil || r == nil || s == nil {
		return false, errors.New("arguments cannot be nil")
	}

	signuture := C.Signature{
		msg_hash:   C.CString(msgHash.Text(16)),
		public_key: C.CString(publicKey.Text(16)),
		r:          C.CString(r.Text(16)),
		s:          C.CString(s.Text(16)),
	}

	valid := C.bool(false)
	errno := C.verify(signuture, &valid)
	if errno != C.Ok {
		return false, errors.New("unknow error")
	}

	return bool(valid), nil
}

func GetPublicKey(privateKey *big.Int) (publicKey *big.Int, err error) {
	if privateKey == nil {
		return nil, errors.New("privateKey is nil")
	}

	privateStr := privateKey.Text(16)
	publicStr := (*C.char)(C.malloc(C.size_t(C.BIG_INT_SIZE)))

	errno := C.get_public_key(C.CString(privateStr), publicStr)

	if errno != C.Ok {
		return nil, errors.New("unknow error")
	}

	publicKey = new(big.Int)
	_, ok := publicKey.SetString(C.GoString(publicStr), 16)
	if !ok {
		return nil, errors.New("publicKey is not a valid big.Int")
	}
	return publicKey, nil
}
