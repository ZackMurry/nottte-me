import { TextField } from '@material-ui/core'
import { useState } from 'react'

export default function LandingPageSignUp() {
    const [ username, setUsername ] = useState('')
    const [ password, setPassword ] = useState('')

    return (
        <div>
            <TextField
                value={username}
                onChange={e => setUsername(e.target.value)}
                variant='outlined'
                // todo make this look better and finish
            />
        </div>
    )
}