import React from 'react'
import { Paper, Typography, Card, CardContent, CardActions, withStyles } from '@material-ui/core'

export default function NotePreview({ name }) {


    return (
        <div style={{margin: 0, cursor: 'pointer'}}>
            <Card>
                <CardContent>
                    <div style={{backgroundColor: '#2d323e', width: '100%', height: '25vh', borderRadius: 10, margin: 0, padding: 0, display: 'flex'}}>
                        <Paper elevation={0} style={{width: '60%', marginLeft: 'auto', marginRight: 'auto', marginBottom: 0, height: '60%', verticalAlign: 'bottom', alignSelf: 'flex-end', borderRadius: '10px 10px 0 0'}}>
                            <Typography style={{margin: 10}}>
                                Note text
                            </Typography>
                        </Paper>
                    </div>
                </CardContent>
                <CardActions>
                    <Typography variant='h4' style={{marginLeft: 10}}>{ name }</Typography>
                </CardActions>
                
            </Card>
            
        </div>
        
    )

}