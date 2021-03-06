import { withStyles, Tooltip } from '@material-ui/core'
import theme from '../theme'

//eslint-disable-next-line
const PlainTooltip = withStyles((theme) => ({
    tooltip: {
        backgroundColor: theme.palette.secondary.main,
        color: theme.palette.primary.main,
        boxShadow: theme.shadows[1],
        fontSize: 14,
        fontWeight: 300
    }
}))(Tooltip)

export default PlainTooltip
