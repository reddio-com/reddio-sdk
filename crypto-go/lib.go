package crypto

/*
#cgo LDFLAGS: -L../output/lib -lcrypto
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
