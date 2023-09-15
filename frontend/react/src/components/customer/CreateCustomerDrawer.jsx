import {
    Button,
    Drawer,
    DrawerOverlay,
    DrawerContent,
    DrawerCloseButton,
    DrawerHeader,
    DrawerFooter,
    DrawerBody, useDisclosure} from "@chakra-ui/react";
import CreateCustomerForm from "../shared/CreateCustomerForm.jsx"


const AddIcon = () => "+";
const CloseIcon = () => "x";

const CreateCustomerDrawer = ({fetchCustomers}) => {
    const { isOpen, onOpen, onClose } = useDisclosure()
    return <div>
        <Button
            leftIcon={<AddIcon/>}
            colorScheme={'red'}
            onClick={onOpen}>
            Create customer
        </Button>

        <Drawer isOpen={isOpen} onClose={onClose} size={"xl"}>
            <DrawerOverlay />
            <DrawerContent>
                <DrawerCloseButton />
                <DrawerHeader>Create new customer</DrawerHeader>

                <DrawerBody>
                    <CreateCustomerForm
                        onSuccess={fetchCustomers}
                    />
                </DrawerBody>

                <DrawerFooter>
                    <Button
                        leftIcon={<CloseIcon/>}
                        colorScheme={"red"}
                        onClick={onClose}>
                        Close
                    </Button>
                </DrawerFooter>
            </DrawerContent>
        </Drawer>
    </div>
}
 export default CreateCustomerDrawer;