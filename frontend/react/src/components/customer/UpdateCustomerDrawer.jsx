import {
    Button,
    Drawer,
    DrawerOverlay,
    DrawerContent,
    DrawerCloseButton,
    DrawerHeader,
    DrawerFooter,
    DrawerBody, useDisclosure, IconButton
} from "@chakra-ui/react";
import {EditIcon} from "@chakra-ui/icons";
import UpdateCustomerForm from "./UpdateCustomerForm.jsx";

const AddIcon = () => "+";
const CloseIcon = () => "x";

const UpdateCustomerDrawer = ({fetchCustomers, initialValues, customerId}) => {
    const { isOpen, onOpen, onClose } = useDisclosure()
    return <div>
        <IconButton
            align={'center'}
            mt={4}
            variant='outline'
            colorScheme='yellow'
            aria-label='Call Sage'
            fontSize='15px'
            icon={<EditIcon />}
            onClick={onOpen}
        />

        <Drawer isOpen={isOpen} onClose={onClose} size={"xl"}>
            <DrawerOverlay />
            <DrawerContent>
                <DrawerCloseButton />
                <DrawerHeader>Update customer</DrawerHeader>

                <DrawerBody>
                    <UpdateCustomerForm
                        fetchCustomers={fetchCustomers}
                        initialValues={initialValues}
                        customerId={customerId}
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
export default UpdateCustomerDrawer;