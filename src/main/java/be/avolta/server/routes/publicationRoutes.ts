import { Router } from 'express';
import { PublicationController } from '../controllers/PublicationController';
import { authMiddleware, roleMiddleware } from '../middleware/auth';

const router = Router();
import { PublicationService } from '../services/PublicationService';

const publicationService = new PublicationService();
const controller = new PublicationController(publicationService);

// Routes publiques
router.get('/publications', controller.getAll.bind(controller));
router.get('/publications/:id', controller.getById.bind(controller));

// Routes protégées
router.use(authMiddleware);
router.post('/publications', controller.create.bind(controller));
router.put('/publications/:id', controller.update.bind(controller));
router.delete('/publications/:id', controller.delete.bind(controller));

// Routes super admin
router.use('/publications', roleMiddleware(['superadmin']));
router.post('/publications/:id/approve', controller.approve.bind(controller));
router.post('/publications/:id/reject', controller.reject.bind(controller));

export default router;