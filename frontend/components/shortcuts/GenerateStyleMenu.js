import { Button, Paper, TextField, Typography } from "@material-ui/core"
import { useState } from "react"
import theme from "../theme"

export default function GenerateStyleMenu({ onCreate }) {

    const [ attribute, setAttribute ] = useState('')
    const [ value, setValue ] = useState('')

    const enterDetection = e => {
        if(e.key == 'Enter') {
            const form = e.target.form
            const index = Array.prototype.indexOf.call(form, e.target)
            form.elements[index+1].focus()
            e.preventDefault()
        }
    }

    const submitDetection = e => {
        if(e.key == 'Enter') {
            e.preventDefault()
            onCreate(attribute, value)
        }
    }



    return (
        <div style={{width: 400, margin: '0 auto'}}>
            <Paper elevation={5} style={{width: 400, height: 40, borderRadius: '5px 5px 0 0', backgroundColor: theme.palette.secondary.main}}>
                <Typography color='primary' style={{padding: '7.5px 10px'}}>
                    Create one-time style (control + m to cancel)
                </Typography>
            </Paper>
            <Paper elevation={5} style={{width: 400, height: 50, borderRadius: '0 0 5px 5px'}}>
                <form style={{padding: 5}}>
                    <TextField
                        value={attribute}
                        onChange={e => setAttribute(e.target.value)}
                        onKeyDown={enterDetection}
                        placeholder='Attribute'
                        style={{width: 125, marginRight: 25}}
                        autoFocus
                    />
                    <TextField
                        value={value}
                        onChange={e => setValue(e.target.value)}
                        placeholder='Value'
                        style={{width: 125, marginRight: 25}}
                        onKeyDown={submitDetection}
                    />
                    <Button
                        onClick={() => onCreate(attribute, value)}
                    >
                        Confirm
                    </Button>
                </form>
            </Paper>
        </div>
        
        
    )

}
