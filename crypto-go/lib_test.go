package crypto

import (
	"testing"

	"github.com/stretchr/testify/assert"
)

func TestSign(t *testing.T) {
	assertor := assert.New(t)
	_, _, err := Sign(nil, nil, nil)
	assertor.NotNil(err)
}
