import {
    Button, FormControl, FormHelperText, MenuItem, Select, Typography
} from '@material-ui/core'
import { useState } from 'react'
import theme from '../theme'

const alreadyExistsError = 'A link share with this permission already exists. You can use that link instead of creating a new one.'
const serverError = 'There was an unknown server error.'
const unknownError = 'There was an unknown error.'

export default function CreateLinkShare({ title, jwt, onCreate }) {
    const [ authority, setAuthority ] = useState('VIEW')
    const [ error, setError ] = useState('')

    const handleCreate = async () => {
        if (!jwt) return

        const requestOptions = {
            method: 'POST',
            headers: { 'Content-Type': 'application/json', 'Authorization': 'Bearer ' + jwt },
            body: JSON.stringify({
                name: title,
                authority
            })
        }

        const response = await fetch('http://localhost:8080/api/v1/shares/link/principal/create', requestOptions)

        console.log(response.status)
        if (response.status === 200) {
            setError('')
            onCreate()
        } else if (response.status === 304) {
            setError(alreadyExistsError)
        } else if (response.status === 500) {
            setError(serverError)
        } else {
            setError(unknownError)
        }
    }

    return (
        <div style={{ marginTop: '2vh' }}>
            <div style={{ display: 'flex', marginTop: 15, justifyContent: 'center' }}>
                <Typography variant='h6' style={{ marginRight: 15 }}>
                    Create new link share
                </Typography>
                <FormControl>
                    <Select
                        labelId='create-link-share-authority-label'
                        id='create-link-share-authority'
                        value={authority}
                        onChange={e => setAuthority(e.target.value)}
                    >
                        <MenuItem value='VIEW'>View</MenuItem>
                        <MenuItem value='EDIT'>Edit</MenuItem>
                    </Select>
                    <FormHelperText>Permission</FormHelperText>
                </FormControl>
                <Button
                    variant='contained'
                    onClick={handleCreate}
                    color='secondary'
                    style={{ height: 35, marginLeft: 15 }}
                >
                    Create
                </Button>

            </div>

            <div hidden={!error} style={{ width: '60%', margin: '1vh auto' }}>
                <Typography variant='body2' style={{ color: theme.palette.error.main, textAlign: 'center' }}>
                    {error}
                </Typography>
            </div>
        </div>

    )
}
