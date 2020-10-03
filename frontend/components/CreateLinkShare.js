import { Button, FormControl, FormHelperText, MenuItem, Select, Typography } from "@material-ui/core";
import { useState } from "react";
import parseJwt from './ParseJwt'
import theme from "./theme";

const alreadyExistsError = "A link share with this permission already exists. You can use that link instead of creating a new one."

export default function CreateLinkShare({ title, jwt, onCreate }) {

    const [ authority, setAuthority ] = useState('VIEW')
    const [ error, setError ] = useState('')

    const handleCreate = async () => {
        if(!jwt) return
        
        const requestOptions = {
            method: 'POST',
            headers: {'Content-Type': 'application/json', 'Authorization': 'Bearer ' + jwt},
            body: JSON.stringify({
                name: title,
                authority: authority,
                author: parseJwt(jwt)?.sub
            })
        }

        const response = await fetch('http://localhost:8080/api/v1/shares/link/principal/create', requestOptions)
        
        console.log(response.status)
        if(response.status == 200) {
            setError('')
            onCreate()
        } else if(response.status == 304) {
            setError(alreadyExistsError)
        } else {
            console.log('bruh')
        }

    }

    return (
        <div style={{marginTop: '2vh'}}>
            <div style={{display: 'flex', marginTop: 15, justifyContent: 'center'}}>
                <Typography variant='h6' style={{marginRight: 15}}>
                    Create new link share
                </Typography>
                <FormControl>
                    <Select
                        labelId='create-link-share-authority-label'
                        id='create-link-share-authority'
                        value={authority}
                        onChange={e => setAuthority(e.target.value)}
                    >
                        <MenuItem value={'VIEW'}>View</MenuItem>
                        <MenuItem value={'EDIT'}>Edit</MenuItem>
                    </Select>
                    <FormHelperText>Permission</FormHelperText>
                </FormControl>
                <Button
                    variant='contained'
                    onClick={handleCreate}
                    color='secondary'
                    style={{height: 35, marginLeft: 15}}
                >
                    Create
                </Button>
                
            </div>

            <div hidden={!error} style={{width: '60%', margin: '1vh auto'}} >
                <Typography variant='body2' style={{color: theme.palette.error.main, textAlign: 'center'}}>
                    {error}
                </Typography>
            </div>
        </div>
        
        
    )

}
