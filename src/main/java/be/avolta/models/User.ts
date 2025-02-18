import { Entity, PrimaryGeneratedColumn, Column, OneToMany } from 'typeorm';
import { Publication } from './Publication';

@Entity('users')
export class User {
  @PrimaryGeneratedColumn('uuid')
  id: string;

  @Column({ unique: true })
  email: string;

  @Column()
  password: string;

  @Column({
    type: 'enum',
    enum: ['admin', 'superadmin'],
    default: 'admin'
  })
  role: 'admin' | 'superadmin';

  @OneToMany(() => Publication, publication => publication.author)
  publications: Publication[];
}