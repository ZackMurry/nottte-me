import { Button, TextField, Typography } from '@material-ui/core'
import { useRouter } from 'next/router'
import { useState } from 'react'

export default function EditPassword({ jwt, style }) {
    const router = useRouter()

    const [ currentPassword, setCurrentPassword ] = useState('')
    const [ newPassword, setNewPassword ] = useState('')
    const [ verifiedPassword, setVerifiedPassword ] = useState('')
    const [ error, setError ] = useState('')
    const [ showSuccess, setShowSuccess ] = useState(false)

    const handleSubmit = async e => {
        e.preventDefault()
        if (newPassword !== verifiedPassword) {
            setError('Your new password must match your verified password.')
            return
        }
        if (!jwt) router.push(`/login?redirect=${encodeURI('/account')}`)

        const requestOptions = {
            method: 'PUT',
            headers: { 'Content-Type': 'application/json', 'Authorization': 'Bearer ' + jwt },
            body: JSON.stringify({
                oldPassword: currentPassword,
                newPassword
            })
        }

        const response = await fetch('http://localhost:8080/api/v1/users/principal/password', requestOptions)
        console.log(response.status)
        if (response.status < 400) {
            setShowSuccess(true)
        } else if (response.status === 403) {
            setError('Your current password is not correct.')
        } else if (response.status === 404) {
            setError('Sorry, but your account couldn\'t be found in our database. Please contact us for help')
        } else if (response.status === 500) {
            setError('There was an internal server error. Please try again later.')
        } else {
            setError('There was an unknown error.')
        }
    }

    const detectEnterContinue = event => {
        if (event.key === 'Enter') {
            const { form } = event.target
            const index = Array.prototype.indexOf.call(form, event.target)
            form.elements[index + 1].focus()
            event.preventDefault()
        }
    }

    const detectEnterSubmit = e => {
        if (e.key === 'Enter') {
            handleSubmit(e)
        }
    }

    return (
        <div style={style}>
            <Typography variant='h5'>
                Change password
            </Typography>

            <form onSubmit={handleSubmit}>
                <div style={{ display: 'inline-flex' }}>
                    <Typography style={{ paddingTop: 7.5 }}>
                        Current password:
                    </Typography>
                    <TextField
                        type='password'
                        value={currentPassword}
                        onChange={e => setCurrentPassword(e.target.value)}
                        onKeyDown={detectEnterContinue}
                        style={{ marginLeft: 5 }}
                    />
                </div>
                <br />
                <div style={{ display: 'inline-flex' }}>
                    <Typography style={{ paddingTop: 7.5 }}>
                        New password:
                    </Typography>
                    <TextField
                        type='password'
                        value={newPassword}
                        onChange={e => setNewPassword(e.target.value)}
                        onKeyDown={detectEnterContinue}
                        style={{ marginLeft: 5 }}
                    />
                </div>
                <br />
                <div style={{ display: 'inline-flex', justifyItems: 'flex-end' }}>
                    <Typography style={{ paddingTop: 7.5 }}>
                        Verify new password:
                    </Typography>
                    <TextField
                        type='password'
                        value={verifiedPassword}
                        onChange={e => setVerifiedPassword(e.target.value)}
                        onKeyDown={detectEnterSubmit}
                        style={{ marginLeft: 5 }}
                    />
                </div>
                <br />
                <div style={{ margin: '1vh 0' }}>
                    <Typography color='error' style={{ fontWeight: 500 }}>
                        {error}
                    </Typography>
                </div>
                <div>
                    <Button
                        type='submit'
                        variant='contained'
                        color='secondary'
                    >
                        Confirm
                    </Button>
                </div>
            </form>

        </div>
    )
}
